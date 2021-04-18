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

import javaff.planning.State;
import java.util.List;
import java.util.Set;

public class BFSSearcher extends Thread
{
  private static ParallelBestFirstSearch searchInstance;


  public static void initialise(ParallelBestFirstSearch searchInstance){
    BFSSearcher.searchInstance = searchInstance;
  }

	public void updateOpen(javaff.planning.State S)
	{
		// 1) get actions applicable in the state S (according to the filter)
		// 2) generate new/children states from state S
		// 3) add the new states to the open list
		// the list is ordered by the h value (lower values go first), so the h value
		// has to be calculated for every new state as it is added to the open list
		// (in the compare(...) method), or before

		List applicableActions = searchInstance.getFilter().getActions(S);
		Set<javaff.planning.State> successorStates = S.getNextStates(applicableActions);

		for(javaff.planning.State state : successorStates)
		  state.getHValue();		// compute the h value

    searchInstance.addAllToOpen(successorStates);
	}

  private static boolean keepSearching(){
    return searchInstance.keepSearching();
  }

	public void search()
	{

		while (keepSearching())
		{
        System.out.println(this.getName() + " - looking for solution");
			javaff.planning.State s = searchInstance.removeNext();

      if(s == null)
        continue;

      if (searchInstance.needToVisit(s))
			{		// expand the node/state
				// ++nodeCount;   // commented out for now

				// check if s contains the goal, if yes return it,
				// else add the children of s to the open list
				if (s.goalReached())
				{
            System.out.println(">>>>>> Solution has been found <<<<<< " + this.getName());
					searchInstance.setSolution(s);
				} else
				{
					updateOpen(s);
				}
			}

		}
	}

  @Override
  public void run(){
    this.search();
  }

}
