/************************************************************************
 * Copyright 2021, Kamil Kuzara
 *
 * This file is part of JavaFF.
 *
 * JavaFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * JavaFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaFF.  If not, see <http://www.gnu.org/licenses/>.
 *
 ************************************************************************/

package javaff.search;

import javaff.data.Action;
import javaff.data.Fact;
import javaff.data.Plan;
import javaff.planning.STRIPSState;
import javaff.planning.State;

import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.math.BigDecimal;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class EHCSearcher extends Thread
{
  private static ParallelEHC searchInstance;

  private LinkedList<StateActionPair> localOpen;
  private Lock localOpenMutex;
  private Condition localOpenNotEmpty;

  private EHCSearcher nextThread;

  public EHCSearcher(){
    localOpen = new LinkedList();
    localOpenMutex = new ReentrantLock();
    localOpenNotEmpty = localOpenMutex.newCondition();
  }

  public void setNextThread(EHCSearcher nextThread){
    this.nextThread = nextThread;
  }

  public static void initialise(ParallelEHC searchInstance){
    EHCSearcher.searchInstance = searchInstance;
  }

  private static boolean keepSearching(){
    return searchInstance.keepSearching();
  }

  private boolean canRemoveItemFromLocal(){
		return ( !searchInstance.isSolutionFound() &&
             !searchInstance.isNewBestStateFound() &&
             !searchInstance.isSearchSpaceExhausted() );
  }

  public void signal(){
    localOpenMutex.lock();
    try{
      localOpenNotEmpty.signal();
    } finally {
      localOpenMutex.unlock();
    }
  }

  private StateActionPair removeNext(){
    localOpenMutex.lock();

    while( localOpen.isEmpty() ){
      if( canRemoveItemFromLocal() ){

				if(searchInstance.getWaitingRoom().tryEntering()){
          try{
            localOpenNotEmpty.await();
          } catch(InterruptedException e){
            e.printStackTrace();
          } finally {
            searchInstance.getWaitingRoom().leave();
          }
        } else {
          searchInstance.searchSpaceExhaustedNotify();
          searchInstance.signalAll(this);
					localOpenMutex.unlock();
					return null;
        }

      } else {
				localOpenMutex.unlock();
				return null;
			}
    }	// end of while-loop

		StateActionPair pair = null;
		try{
			pair = localOpen.pollFirst();	// retrieves and removes the first item or returns null
		} finally {
			localOpenMutex.unlock();
		}

		return pair;
  }

  public void clearLocalOpen(){
    localOpenMutex.lock();
    try{
      localOpen.clear();
    } finally {
      localOpenMutex.unlock();
    }
  }

  public void addAllToOpen(List<StateActionPair> newPairs){
    localOpenMutex.lock();
    try{
      localOpen.addAll(newPairs);

      if(!localOpen.isEmpty())
        localOpenNotEmpty.signal();
    } finally {
      localOpenMutex.unlock();
    }
  }

  /**
	 * Tests whether any of the actions in the RELAXED plan associated with this state
	 * delete a goal.
	 * @param relaxedPlan The relaxed plan which will be used to detect goal orderings
	 * @param goal The goal to check
	 * @return
	 */
	private boolean isGoalThreatened(Plan relaxedPlan, Fact goal)
	{
		//maintain a list of achieved goals as we go through the RP in-order. These will
		//be checked at each timestep to see if any already achieved goals are deleted.
		HashSet<Fact> achieved = new HashSet<Fact>();
		List<Action> actions = relaxedPlan.getActions();


		for (Action a : actions)
		{
			for (Fact g : goal.getFacts())
			{
				//if this action deletes a goal and that goal has already been achieved by
				//a previous action in the RP, immediately return
				if (a.deletes(g) && achieved.contains(g))
					return true;
			}

			achieved.addAll(a.getAddPropositions());
			achieved.addAll(a.getDeletePropositions()); //needed for ADL goals
		}

		return false;
	}

  private void search()
	{
    while(keepSearching()){

      searchInstance.waitForBarriers();

      if(searchInstance.isSolutionFound())
        continue;

      StateActionPair pair = removeNext();
      if(pair == null)
        continue;

      // generate the new state
			javaff.planning.State succState = pair.getState().getNextState(pair.getAction());

			// check if belongs to closed
			if( searchInstance.alreadyVisited(succState) )
				continue;

			BigDecimal succH = succState.getHValue();

			//check we have not entered a dead-end
			if (((STRIPSState) succState).getRelaxedPlan() == null)
				continue;

			// do online goal-ordering check -- this is used by FF to prevent deleting goals early in the
			// relaxed plan, which would then need negated again later on
			boolean threatensGoal = isGoalThreatened(((STRIPSState) succState).getRelaxedPlan(), succState.goal);
			if(threatensGoal){
				searchInstance.updateClosed(succState);
				continue;
			}

			boolean newBestFoundLocally = false;
			if(succState.goalReached()){
				searchInstance.setSolution(succState);
				searchInstance.signalAll(this);
			} else {
				searchInstance.updateClosed(succState);
				newBestFoundLocally = searchInstance.checkIfNewBest(succH);
			}

			if(newBestFoundLocally){
				searchInstance.signalAll(this);
				searchInstance.resetSearch(succState);
			} else {
				List<StateActionPair> newPairs = new LinkedList();
				for(Action action : searchInstance.getFilter().getActions(succState))
					newPairs.add(new StateActionPair(succState, action));

				nextThread.addAllToOpen(newPairs);
			}
		}
	}

  @Override
  public void run(){
    search();
  }





// -----------------------------------------------------------------------------

  // public static State getSolution(){
  //   State rState = null;
  //
  //   solutionMutex.lock();
	// 	try{
	// 		rState = solution;
	// 	} finally {
	// 		solutionMutex.unlock();
	// 	}
  //
  //   return rState;
  // }

  // public static void setBestHValue(BigDecimal hValue){
  //   bestHMutex.lock();
	// 	try{
	// 		bestHValue = hValue;
	// 	} finally {
	// 		bestHMutex.unlock();
	// 	}
  // }
}
