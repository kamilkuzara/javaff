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
import javaff.data.Action;
// import javaff.planning.Filter;
// import java.util.Comparator;
import java.util.TreeSet;
// import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
// import java.math.BigDecimal;

public class BFSWorker extends Thread
{

  private static LinkedList<Action> actions;
  private static TreeSet open;
  private static javaff.planning.State state;
  private static Lock openMutex;

  private Set localOpen;

  public BFSWorker(){
    localOpen = new HashSet();
  }

  public static void initialise(TreeSet open, Lock mutex){
    BFSWorker.open = open;
    BFSWorker.openMutex = mutex;
  }

  public static void reset(javaff.planning.State s, LinkedList actions){
    setActions(actions);
    setState(s);
  }

  private static void setActions(LinkedList actions){
    BFSWorker.actions = actions;
  }

  private static void setState(javaff.planning.State s){
    BFSWorker.state = s;
  }

  public void reset(){
    localOpen = new HashSet();
  }

  public void run(){

    boolean finished = false;
    while(!finished){
      Action action = null;
        System.out.println("Before getting an action");
      synchronized(BFSWorker.actions){
        action = BFSWorker.actions.pollFirst();   // remove first or return null
      }
        System.out.println("After getting an action");

      if(action == null){

        openMutex.lock();
        try{
          BFSWorker.open.addAll(localOpen);
        }finally{
          openMutex.unlock();
        }
          System.out.println("Return");
        finished = true;
        continue;
      }

      javaff.planning.State s = BFSWorker.state.getNextState(action);
      s.getHValue();
      localOpen.add(s);

      if(openMutex.tryLock()){
        try{
          BFSWorker.open.addAll(localOpen);
        }finally{
          openMutex.unlock();
        }
      }
    }

    // BFSWorker.semaphore.release();
  }

}
