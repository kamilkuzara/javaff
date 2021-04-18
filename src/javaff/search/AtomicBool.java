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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AtomicBool
{
  private boolean value;
  private Lock mutexRead;
  private Lock mutexWrite;

  public AtomicBool(){
    this(false);
  }

  public AtomicBool(boolean valueIn){
    this.value = valueIn;

    ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    mutexRead = mutex.readLock();
    mutexWrite = mutex.writeLock();
  }

  /*
   * Allows only one thread at a time
   */
  public void set(boolean newValue){

    mutexWrite.lock();
    try{
      this.value = newValue;
    } finally {
      mutexWrite.unlock();
    }

  }

  /*
   * Allows multiple threads at a time to read the value
   */
  public boolean get(){
    boolean rValue = false;

    mutexRead.lock();
    try{
      rValue = this.value;
    } finally {
      mutexRead.unlock();
    }

    return rValue;
  }

}
