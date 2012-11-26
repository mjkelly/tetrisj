/*-----------------------------------------------------------------
* TetradSquare.java -- Tetris piece.
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Fri Dec 10 23:39:32 PST 2004
-----------------------------------------------------------------*/
package TetrisJ;

import java.awt.Point;

/** A 2x2 square Tetrad. */
public class TetradSquare extends Tetrad {
	public TetradSquare(){
		this(new Point(0, 0));
	}

	public TetradSquare(Point p){
		center = new Tile(p, Tile.BLUE);
		tiles = new Tile[3];
		tiles[0] = new Tile(new Point(1, 0), Tile.BLUE);
		tiles[1] = new Tile(new Point(1, 1), Tile.BLUE);
		tiles[2] = new Tile(new Point(0, 1), Tile.BLUE);
	}

	/* squares don't rotate. it would look funny. */
	public void rotateCW(){}
	public void rotateCCW(){}
}

