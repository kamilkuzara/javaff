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
// import javaff.planning.Filter;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.Semaphore;

public class ParallelBestFirstSearch extends Search
{
	private static final int NUM_THREADS = 4;

	private LinkedList<Thread> threads;
	private WaitingRoom waitingRoom;

	protected Hashtable closed;

	protected TreeSet open;
	private Lock openMutex;
	private Condition openNotEmpty;

	private State solution;
	private AtomicBool solutionFound;
	private Lock solutionMutex;

	private AtomicBool stateSpaceExhausted;

	public ParallelBestFirstSearch(State s)
	{
		this(s, new HValueComparator());
	}

	public ParallelBestFirstSearch(State s, Comparator c)
	{
		super(s);
		setComparator(c);

		closed = new Hashtable();

		open = new TreeSet(comp);
		openMutex = new ReentrantLock();
		openNotEmpty = openMutex.newCondition();

		solutionFound = new AtomicBool(false);
    solution = null;
		solutionMutex = new ReentrantLock();

		stateSpaceExhausted = new AtomicBool(false);

		BFSSearcher.initialise(this);

		threads = new LinkedList();

    for(int i = 0; i < NUM_THREADS; i++)
      threads.add(new BFSSearcher());

		waitingRoom = new WaitingRoom(NUM_THREADS - 1);
	}

	public boolean keepSearching(){
		return !solutionFound.get() && !stateSpaceExhausted.get();
	}

  public void setSolution(State solutionIn){
		solutionMutex.lock();
		try{
			solution = solutionIn;
			solutionFound.set(true);	// note that another lock will have to be acquired here
		} finally {
			solutionMutex.unlock();
		}

		openMutex.lock();
		try{
			openNotEmpty.signal();
		} finally {
			openMutex.unlock();
		}
  }

	public void addAllToOpen(Set<State> newStates){
    openMutex.lock();
    try{
			open.addAll(newStates);

				System.out.println("Open size: " + open.size());

			if(!open.isEmpty()){	 // this conditional stmt is not strictly necessary
				openNotEmpty.signal();
					System.out.println("Signalling");
			}
    } finally {
      openMutex.unlock();
    }
  }

	public State removeNext()
	{
    State S = null;

		openMutex.lock();

		while(open.isEmpty()){

			if(keepSearching()){
				if(waitingRoom.tryEntering()){
					try{
							System.out.println("Before await");
						openNotEmpty.await();
					} catch(InterruptedException e){
						e.printStackTrace();
					} finally {
						waitingRoom.leave();
						System.out.println("After await");
					}
				} else {
					stateSpaceExhausted.set(true);
					openNotEmpty.signal();
					openMutex.unlock();
					return null;
				}
			} else {
				openNotEmpty.signal();
				openMutex.unlock();
				return null;
			}

		}

		try{
			S = (State) (open).first();
			open.remove(S);
				System.out.println("Removing first state");
		} finally {
			openMutex.unlock();
		}

    return S;
	}

	public boolean needToVisit(State s)
	{
		Integer Shash = new Integer(s.hashCode());
    boolean rValue = false;

    synchronized (closed) {
  		State D = (State) closed.get(Shash);

  		if (closed.containsKey(Shash) && D.equals(s))
  			rValue = false;
      else {
        closed.put(Shash, s);
        rValue = true;
      }
    }

		return rValue;
	}

	public void updateOpen(State S)
	{
		// 1) get actions applicable in the state S (according to the filter)
		// 2) generate new/children states from state S
		// 3) add the new states to the open list
		// the list is ordered by the h value (lower values go first), so the h value
		// has to be calculated for every new state as it is added to the open list
		// (in the compare(...) method), or before

		List applicableActions = getFilter().getActions(S);
		Set<State> successorStates = S.getNextStates(applicableActions);

		for(State state : successorStates)
			state.getHValue();		// compute the h value

    addAllToOpen(successorStates);
	}

	public State search()
	{

		open.add(start);

		// start all the threads except the last one,
		// the last one will be executed in the main thread (see below)
		for(int i = 0; i < NUM_THREADS - 1; i++)
			threads.get(i).start();

		// execute the last worker in the main thread
		threads.getLast().run();

    State rState = null;
		solutionMutex.lock();
    try{
      rState = solution;
    } finally {
			solutionMutex.unlock();
		}

    return rState;
	}

}
