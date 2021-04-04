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


public class WaitingRoom
{
  private int openSlots;

  public WaitingRoom(int openSlots){
    this.openSlots = openSlots;
  }

  public synchronized boolean tryEntering(){
    if(openSlots > 0){
      openSlots--;
      return true;
    }
    else
      return false;
  }

  public synchronized void leave(){
    openSlots++;
  }
}
