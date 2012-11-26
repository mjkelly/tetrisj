/*-----------------------------------------------------------------
* EndGameException.java -- Exception to be thrown when the game ends
* unexpectedly.
*
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Wed Dec 29 22:19:21 PST 2004
-----------------------------------------------------------------*/
package TetrisJ;

/**
* Exception to be thrown when the game ends unexpectedly.
*/
public class EndGameException extends RuntimeException{
	/**
	* Create exception with default text.
	*/
	public EndGameException(){
		super("Game ended.");
	}
	/**
	* Create exception with given text.
	* @param msg Exception message.
	*/
	public EndGameException(String msg){
		super(msg);
	}
}

