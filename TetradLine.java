/*-----------------------------------------------------------------
* TetradLine.java -- Tetris piece.
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Sat Dec 11 00:14:04 PST 2004
-----------------------------------------------------------------*/
package TetrisJ;

import java.awt.Point;

/** A vertical line Tetrad. */
public class TetradLine extends Tetrad {
	/* a little flag to indicate when we could reverse rotational direction,
	 * since we don't really rotate the line. */
	boolean flip;

	public TetradLine(){
		this(new Point(0, 0));
		this.flip = false;
	}

	public TetradLine(Point p){
		center = new Tile(p, Tile.GRAY);
		tiles = new Tile[3];
		tiles[0] = new Tile(new Point(0, -1), Tile.GRAY);
		tiles[1] = new Tile(new Point(0, 1), Tile.GRAY);
		tiles[2] = new Tile(new Point(0, 2), Tile.GRAY);
	}

	/* we don't actually rotate. we flip between two positions. I think it's
	 * lame, but that's how other tetris programs do it, so we shouldn't
	 * confuse people... */
	/* both of these methods are the same */
	public void rotateCW(){
		if(flip){
			super.rotateCW();
			flip = false;
		}
		else{
			super.rotateCCW();
			flip = true;
		}
	}
	public void rotateCCW(){
		if(flip){
			super.rotateCW();
			flip = false;
		}
		else{
			super.rotateCCW();
			flip = true;
		}
	}
}

