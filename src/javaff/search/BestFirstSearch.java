/************************************************************************
 * Strathclyde Planning Group,
 * Department of Computer and Information Sciences,
 * University of Strathclyde, Glasgow, UK
 * http://planning.cis.strath.ac.uk/
 *
 * Copyright 2007, Keith Halsey
 * Copyright 2008, Andrew Coles and Amanda Smith
 * Copyright 2015, David Pattison
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
import javaff.planning.Filter;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;

public class BestFirstSearch extends Search
{

	protected Hashtable closed;
	protected TreeSet open;

	private BigDecimal heuristicsTime;
	private int statesGeneratedCount;

	public BestFirstSearch(State s)
	{
		this(s, new HValueComparator());
	}

	public BestFirstSearch(State s, Comparator c)
	{
		super(s);
		setComparator(c);

		closed = new Hashtable();
		open = new TreeSet(comp);
		heuristicsTime = BigDecimal.ZERO;
		statesGeneratedCount = 0;
	}

	public void updateOpen(State S)
	{
		// 1) get actions applicable in the state S (according to the filter)
		// 2) generate new/children states from state S
		// 3) add the new states to the open list
		// the list is ordered by the h value (lower values go first), so the h value
		// has to be calculated for every new state as it is added to the open list
		// (in the compare(...) method), or before

		List applicableActions = filter.getActions(S);
		Set<State> successorStates = S.getNextStates(applicableActions);
		statesGeneratedCount += successorStates.size();

		long startTime = 0;
		long endTime = 0;

		// compute the heuristic values for the states and measure the time needed
		startTime = System.nanoTime();

		open.addAll(successorStates);	// add the states to the open list

		endTime = System.nanoTime();
		heuristicsTime = heuristicsTime.add(BigDecimal.valueOf(endTime - startTime));
	}

	public State removeNext()
	{
		State S = (State) (open).first();
		open.remove(S);
		/*
		 * System.out.println("================================");
		 * S.getSolution().print(System.out); System.out.println("----Helpful
		 * Actions-------------"); javaff.planning.TemporalMetricState ms =
		 * (javaff.planning.TemporalMetricState) S;
		 * System.out.println(ms.helpfulActions);
		 * System.out.println("----Relaxed Plan----------------");
		 * ms.RelaxedPlan.print(System.out);
		 */
		return S;
	}

	public boolean needToVisit(State s)
	{
		Integer Shash = new Integer(s.hashCode());
		State D = (State) closed.get(Shash);

		if (closed.containsKey(Shash) && D.equals(s))
			return false;

		closed.put(Shash, s);
		return true;
	}

	public State search()
	{

		open.add(start);

		while (!open.isEmpty())
		{
			State s = removeNext();
			if (needToVisit(s))
			{		// expand the node/state
				++nodeCount;

				// check if s contains the goal, if yes return it,
				// else add the children of s to the open list
				if (s.goalReached())
				{
					double hTime = getHeuristicsTime();
					System.out.println("-------------------- Statistics --------------------");
					System.out.println("Total time computing heuristics: " + hTime + " sec");
					System.out.println("States expanded: " + nodeCount);
					System.out.println("States generated: " + statesGeneratedCount);
					System.out.println("----------------------------------------------------");
					return s;
				} else
				{
					updateOpen(s);
				}
			}

		}
		return null;
	}

	public double getHeuristicsTime(){
		return heuristicsTime.divide(BigDecimal.valueOf(1000000000)).doubleValue();
	}

}
