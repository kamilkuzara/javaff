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

import javaff.JavaFF;
import javaff.data.Action;
import javaff.data.Fact;
import javaff.data.Plan;
import javaff.data.TotalOrderPlan;
import javaff.data.strips.NullFact;
import javaff.data.strips.RelaxedFFPlan;
import javaff.planning.HelpfulFilter;
import javaff.planning.NullFilter;
import javaff.planning.STRIPSState;
import javaff.planning.State;
import javaff.planning.Filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import java.util.Comparator;
import java.math.BigDecimal;

import java.util.Hashtable;
import java.util.Iterator;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.BrokenBarrierException;

public class ParallelEHC extends Search
{
	private static final int NUM_THREADS = 4;

	private State solution;
  private Lock solutionMutex;

  // flags
  private AtomicBool solutionFound;
  private AtomicBool newBestStateFound;
  private AtomicBool searchSpaceExhausted;

  private BigDecimal bestHValue;
  private Lock bestHMutex;

  private CyclicBarrier barrierA;
  private CyclicBarrier barrierB;

  private WaitingRoom waitingRoom;

  private List<EHCSearcher> threads;

	//cache the hash codes rather than the states for memory efficiency
	private HashSet<Integer> closed;

	public ParallelEHC(State s)
	{
		this(s, new HValueComparator());
	}

	public ParallelEHC(State s, Comparator c)
	{
		super(s);
		setComparator(c);

		solution = null;
		solutionMutex = new ReentrantLock();

		solutionFound = new AtomicBool(false);
	  newBestStateFound = new AtomicBool(false);
	  searchSpaceExhausted = new AtomicBool(false);

		bestHMutex = new ReentrantLock();

		barrierA = new CyclicBarrier(NUM_THREADS);
    barrierB = new CyclicBarrier(NUM_THREADS);

		waitingRoom = new WaitingRoom(NUM_THREADS - 1);

		// create all threads
		threads = new LinkedList();
		for(int i = 0; i < NUM_THREADS; i++)
      threads.add(new EHCSearcher());

		// link threads together in a circle, e.g. T0 -> T1 -> T2 -> T0
		for(int i = 0; i < NUM_THREADS; i++){
			EHCSearcher current = threads.get(i);
			EHCSearcher next = threads.get( (i + 1) % NUM_THREADS );
			current.setNextThread(next);
		}

		EHCSearcher.initialise(this);

		closed = new HashSet<Integer>();
	}

	public boolean keepSearching(){
		return ( !solutionFound.get() || newBestStateFound.get() ) && !searchSpaceExhausted.get();
  }

	public void waitForBarriers(){
		if( newBestStateFound.get() ){
			try{
				barrierA.await();
	      barrierB.await();
			} catch(InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
    }
  }

	public boolean isSolutionFound(){
		return solutionFound.get();
  }

	public boolean isNewBestStateFound(){
		return newBestStateFound.get();
  }

	public boolean isSearchSpaceExhausted(){
		return searchSpaceExhausted.get();
  }

	public WaitingRoom getWaitingRoom(){
		return waitingRoom;
	}

	public void searchSpaceExhaustedNotify(){
		searchSpaceExhausted.set(true);
  }

	public void signalAll(EHCSearcher invokingThread){
		for(EHCSearcher thread : threads){
			// NOTE: the if-stmt should not be necessary to prevent deadlock
			// because reentrant locks are used (they can be locked multiple
			// times by the same thread)
			// if(thread == invokingThread)
			// 	continue;

			thread.signal();
		}
	}

	public boolean alreadyVisited(State state){
		boolean rValue = false;

		synchronized(closed){
			rValue = closed.contains(state.hashCode());
		}

		return rValue;
	}

	public void updateClosed(State state){
		synchronized(closed){
			closed.add(state.hashCode());
		}
	}

	public void setSolution(State state){
		solutionMutex.lock();
		try{
			solution = state;
			solutionFound.set(true);	// note that another lock has to be acquired for this
		} finally {
			solutionMutex.unlock();
		}
	}

	public boolean checkIfNewBest(BigDecimal hValue){
		boolean newBest = false;

		bestHMutex.lock();
		try{
			if( (hValue.compareTo(bestHValue) < 0) && !newBestStateFound.get() && !solutionFound.get()){
				bestHValue = hValue;
				newBestStateFound.set(true);
				newBest = true;
			}
		} finally {
			bestHMutex.unlock();
		}

		return newBest;
	}

	public void resetSearch(State state){
		try{
			barrierA.await();
		} catch(InterruptedException | BrokenBarrierException e){
			e.printStackTrace();	// TODO: need to implement proper error handling
		}
		barrierA.reset();	// reset the barrier to initial state after it was broken

		// clear all local open lists
		for(EHCSearcher thread : threads)
			thread.clearLocalOpen();

		// fill the starting open list with children of the current best state
		List<StateActionPair> newPairs = new LinkedList();
		for(Action action : filter.getActions(state))
			newPairs.add(new StateActionPair(state, action));

		threads.get(0).addAllToOpen(newPairs);

		newBestStateFound.set(false);	// very important to reset the flag!

		try{
			barrierB.await();
		} catch(InterruptedException | BrokenBarrierException e){
			e.printStackTrace();	// TODO: need to implement proper error handling
		}
		barrierB.reset();	// reset the barrier to initial state after it was broken
	}

	public State search(){
		if(start.goalReached())
		{ // wishful thinking
			return start;
		}

		updateClosed(start);

		// fill the starting open list with children of the current best state
		List<StateActionPair> newPairs = new LinkedList();
		for(Action action : filter.getActions(start))
			newPairs.add(new StateActionPair(start, action));

		threads.get(0).addAllToOpen(newPairs);

		bestHValue = start.getHValue();	// get h value of the initial state as the best so far

		// start the first n - 1 threads
		for(int i = 0; i < threads.size() - 1; i++)
			threads.get(i).start();

		threads.get(NUM_THREADS - 1).run();	// run in the main thread so that it doesn't go idle

		// wait for the first n - 1 threads to finish after the main thread finished
		for(int i = 0; i < threads.size() - 1; i++){
			try{
				threads.get(i).join();
			} catch(InterruptedException e){
				e.printStackTrace();	// TODO: need to implement proper error handling
			}
		}

		return solution;	// only one thread at this point, no need to lock
	}

}
