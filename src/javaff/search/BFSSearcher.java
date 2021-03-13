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
// import java.math.BigDecimal;

public class BFSSearcher extends Thread
{
  private ParallelBestFirstSearch searchInstance;

	// private BigDecimal heuristicsTime;

	public BFSSearcher(ParallelBestFirstSearch searchIn)
	{
    searchInstance = searchIn;

		// heuristicsTime = BigDecimal.ZERO;
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

		// long startTime = 0;
		// long endTime = 0;
		for(javaff.planning.State state : successorStates)
		{
			// compute the heuristic value for the state and measure the time needed
			// startTime = System.nanoTime();
			state.getHValue();		// compute the h value
			// endTime = System.nanoTime();
			// heuristicsTime = heuristicsTime.add(BigDecimal.valueOf(endTime - startTime));
		}

    searchInstance.updateOpen(successorStates);
	}

  private boolean keepSearching(){
    return !this.searchInstance.isSolutionFound().get();
  }

	public void search()
	{

		while (keepSearching())
		{
        System.out.println(this.getName() + " - looking for solution");
			javaff.planning.State s = searchInstance.removeNext();
			if (searchInstance.needToVisit(s))
			{		// expand the node/state
				// ++nodeCount;   // commented out for now
          String x = (s != null)?"1":"null";
          System.out.println("checking state - " + x);
				// check if s contains the goal, if yes return it,
				// else add the children of s to the open list
				if (s.goalReached())
				{
					// double hTime = heuristicsTime.divide(BigDecimal.valueOf(1000000000)).doubleValue();
					// System.out.println("Total time computing heuristics: " + hTime);
            System.out.println(">>>>>> Solution has been found <<<<<< " + this.getName());
					searchInstance.setSolution(s);
				} else
				{
					updateOpen(s);
				}
			}

		}
		// searchInstance.setSolution(null);
	}

  @Override
  public void run(){
    this.search();
  }

}
