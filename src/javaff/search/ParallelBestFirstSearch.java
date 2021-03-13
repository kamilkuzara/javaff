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
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
// import java.math.BigDecimal;

public class ParallelBestFirstSearch extends Search
{

	protected Hashtable closed;
	protected TreeSet open;

  private AtomicBoolean solutionFound;
  private State solution;

	// private BigDecimal heuristicsTime;

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

    solutionFound = new AtomicBoolean(false);
    solution = null;
		// heuristicsTime = BigDecimal.ZERO;
	}

  public AtomicBoolean isSolutionFound(){
    return solutionFound;
  }

  public synchronized void setSolution(State solutionIn){
    this.solution = solutionIn;

    solutionFound.set(true);

    String x = (this.solution != null)?"1":"null";
    String y = (solutionIn != null)?"1":"null";
    System.out.println("assigning final state - " + x + " - " + y);
  }

	public synchronized void updateOpen(Set<State> newStates){
    open.addAll(newStates);
  }

	public State removeNext()
	{
    boolean stateAcquired = false;
    State S = null;

    while(!stateAcquired){
      synchronized(this){
        if(!open.isEmpty()){
          S = (State) (open).first();
      		open.remove(S);

          stateAcquired = true;
        }
      }
    }

    return S;
	}

	public boolean needToVisit(State s)
	{
		Integer Shash = new Integer(s.hashCode());
    boolean rValue = false;

    synchronized (this) {
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

	public State search()
	{

		open.add(start);

    List<Thread> threads = new ArrayList();

    for(int i = 0; i < 4; i++){
      Thread t = new BFSSearcher(this);
      t.start();
      threads.add(t);
    }

    // for(Thread t : threads){
    //   try{
    //     t.join();
    //   }catch(InterruptedException e){
    //     e.printStackTrace();
    //   }
    // }

    while(!solutionFound.get()){
      try{
        Thread.sleep(50);
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }

    State rState = null;
    synchronized(this){
        System.out.println("Assigning state to return");
        String x = (this.solution != null)?"1":"null";
        System.out.println("final state - " + x);
      rState = this.solution;
    }

    return rState;
	}

}
