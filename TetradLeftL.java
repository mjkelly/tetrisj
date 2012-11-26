/*-----------------------------------------------------------------
* TetradLeftL.java -- Tetris piece.
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

/** A left-facing "L" Tetrad. */
public class TetradLeftL extends Tetrad {
	public TetradLeftL(){
		this(new Point(0, 0));
	}

	public TetradLeftL(Point p){
		center = new Tile(p, Tile.YELLOW);
		tiles = new Tile[3];
		tiles[0] = new Tile(new Point(0, -1), Tile.YELLOW);
		tiles[1] = new Tile(new Point(0, 1), Tile.YELLOW);
		tiles[2] = new Tile(new Point(1, 1), Tile.YELLOW);
	}
}

