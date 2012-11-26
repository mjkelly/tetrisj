/*-----------------------------------------------------------------
* Debug.java -- A very simple debugging class.
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Sat Jan  1 23:23:59 PST 2005
-----------------------------------------------------------------*/
package TetrisJ;

/**
* Debugging class to provide simple output functions.
*/
public class Debug {
	/**
	* Current debugging level.
	*/
	public static int LEVEL = 0;
	/**
	* Print the given message if debugging is at least level <code>l</code>.
	* @param l   Minimum debugging level required to print message.
	* @param msg Message to print.
	*/
	public static void p(int l, String msg){
		if(LEVEL >= l) System.err.println(msg);
	}
	/**
	* Print the given message if debugging is at least level 1.
	* @param msg Message to print.
	*/
	public static void p(String msg){
		p(1, msg);
	}
}

