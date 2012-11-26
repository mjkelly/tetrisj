/*-----------------------------------------------------------------
* TetradT.java -- Tetris piece.
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

/** A "T"-shaped Tetrad. */
public class TetradT extends Tetrad {
	public TetradT(){
		this(new Point(0, 0));
	}

	public TetradT(Point p){
		center = new Tile(p, Tile.MAGENTA);
		tiles = new Tile[3];
		tiles[0] = new Tile(new Point(0, -1), Tile.MAGENTA);
		tiles[1] = new Tile(new Point(1, 0), Tile.MAGENTA);
		tiles[2] = new Tile(new Point(0, 1), Tile.MAGENTA);
	}
}

