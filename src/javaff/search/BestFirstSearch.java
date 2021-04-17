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
// import java.util.concurrent.Semaphore;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
// import java.math.BigDecimal;

public class BestFirstSearch extends Search
{
	private static final int NUM_THREADS = 4;

	protected Hashtable closed;
	protected TreeSet open;

	// private BigDecimal heuristicsTime;
	private LinkedList<BFSWorker> workerThreads;
	private Lock openMutex;
	private CyclicBarrier barrierA;
	private CyclicBarrier barrierB;
	private CyclicBarrier barrierC;

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

		barrierA = new CyclicBarrier(NUM_THREADS);
		barrierB = new CyclicBarrier(NUM_THREADS);
		barrierC = new CyclicBarrier(NUM_THREADS);

		BFSWorker.initialise(open, openMutex, barrierA, barrierB, barrierC);

		workerThreads = new LinkedList();
		for(int i = 0; i < NUM_THREADS; i++)
			workerThreads.add(new BFSWorker());

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

		LinkedList<Action> applicableActions = new LinkedList(filter.getActions(S));
		BFSWorker.reset(S, applicableActions);

		try {
			barrierA.await();
		} catch(InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		barrierA.reset();

		try {
			barrierB.await();
		} catch(InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		barrierB.reset();

		workerThreads.getLast().computeHValues();

		try {
			barrierC.await();
		} catch(InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		barrierC.reset();

			System.out.println("New states added");
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

		// start all workers except the last one
		// the last one is executed by the main thread
		for(int i = 0; i < NUM_THREADS - 1; i++){
			workerThreads.get(i).start();
				System.out.println("Thread started");
		}

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
					terminateSearch();

					return s;
				} else
				{
					updateOpen(s);
				}
			}

		}

		terminateSearch();

		return null;
	}

	private void terminateSearch(){

		try {
			barrierA.await();
		} catch(InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}

		BFSWorker.searchFinishedNotify();

		try {
			barrierB.await();
		} catch(InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}

	}

}
