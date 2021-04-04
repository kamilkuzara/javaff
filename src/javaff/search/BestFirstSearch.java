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
import javaff.data.Action;
import javaff.planning.Filter;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.math.BigDecimal;

public class BestFirstSearch extends Search
{
	private static final int NUM_THREADS = 4;

	protected Hashtable closed;
	protected TreeSet open;

	// private BigDecimal heuristicsTime;
	private Lock openMutex;

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
		openMutex = new ReentrantLock();
		BFSWorker.initialise(open, openMutex);

		// heuristicsTime = BigDecimal.ZERO;
	}

	public void updateOpen(State S)
	{
		System.out.println("Update open");
		// 1) get actions applicable in the state S (according to the filter)
		// 2) generate new/children states from state S
		// 3) add the new states to the open list
		// the list is ordered by the h value (lower values go first), so the h value
		// has to be calculated for every new state as it is added to the open list
		// (in the compare(...) method), or before

		List<BFSWorker> workerThreads = new ArrayList();
		for(int i = 0; i < NUM_THREADS; i++)
			workerThreads.add(new BFSWorker());

		LinkedList<Action> applicableActions = new LinkedList(filter.getActions(S));
		BFSWorker.reset(S, applicableActions);

		for(BFSWorker t : workerThreads){
			t.reset();
			t.start();
				System.out.println("Thread started");
		}

		Set localOpen = new HashSet();
		boolean finished = false;
    while(!finished){
      Action action = null;
        System.out.println("Before getting an action");
      synchronized(applicableActions){
        action = applicableActions.pollFirst();   // remove first or return null
      }
        System.out.println("After getting an action");

      if(action == null){

        openMutex.lock();
        try{
          open.addAll(localOpen);
        }finally{
          openMutex.unlock();
        }
          System.out.println("Return");
        finished = true;
        continue;
      }

      State s = S.getNextState(action);
      s.getHValue();
      localOpen.add(s);

      if(openMutex.tryLock()){
        try{
          open.addAll(localOpen);
        }finally{
          openMutex.unlock();
        }
      }
    }

		for(BFSWorker t : workerThreads){
			try{
				t.join();
					System.out.println("	Thread join");
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}

		// Set<State> successorStates = S.getNextStates(applicableActions);
		//
		// long startTime = 0;
		// long endTime = 0;
		// for(State state : successorStates)
		// {
		// 	// compute the heuristic value for the state and measure the time needed
		// 	startTime = System.nanoTime();
		// 	state.getHValue();		// compute the h value
		// 	endTime = System.nanoTime();
		// 	heuristicsTime = heuristicsTime.add(BigDecimal.valueOf(endTime - startTime));
		//
		// 	open.add(state);	// add the state to the open list
		// }
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
					// double hTime = heuristicsTime.divide(BigDecimal.valueOf(1000000000)).doubleValue();
					// System.out.println("Total time computing heuristics: " + hTime);
					return s;
				} else
				{
					updateOpen(s);
				}
			}

		}
		return null;
	}

}
