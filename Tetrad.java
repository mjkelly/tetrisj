/*-----------------------------------------------------------------
* Tetrad.java -- Tetris pieces.
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Wed May  4 16:55:55 PDT 2005
-----------------------------------------------------------------*/
package TetrisJ;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.util.Random;

/**
* A generic tetris piece. The ones we actually use are subclasses of this.
*/
public abstract class Tetrad {
	/** The center square. */
	public Tile center;
	/** The locations of the other tiles, AS OFFSETS FROM THE CENTER. */
	public Tile[] tiles;

	/** Where we get random numbers. This is a class variable to prevent
	 * multiple calls to <code>randomTetrad</code> within the same
	 * milisecond from generating the same numbers. Don't laugh, it
	 * happens! */
	private static Random r;

	/** Create a new Tetrad at (0, 0). */
	public Tetrad(){
		this(new Point(0, 0));
	}
	/**
	* Create a new Tetrad at the given point.
	* @param p Point on which to center new Tetrad.
	*/
	public Tetrad(Point p){
		center = new Tile(p);
	}

	/** Rotate the whole tetrad 90 degrees clockwise around its center. */
	public void rotateCW(){
		for(int i = 0; i < tiles.length; i++){
			rotateTileCW(tiles[i]);
		}
	}

	/** Rotate the whole tetrad 90 degrees counter-clockwise around its
	 * center. */
	public void rotateCCW(){
		for(int i = 0; i < tiles.length; i++){
			rotateTileCCW(tiles[i]);
		}
	}
	
	/** Move a single tile 90 degrees clockwise around the origin. */
	private static void rotateTileCW(Tile p){
		p.setLocation(-(int)p.getY(), (int)p.getX());
	}
	/** Move a single tile 90 degrees counter-clockwise around the origin. */
	private static void rotateTileCCW(Tile p){
		p.setLocation((int)p.getY(), -(int)p.getX());
	}

	/**
	* Get the absolute X location (not offset) of the <code>i</code>th tile.
	* @param i index of tile
	* @return the absolute location of tile index <code>i</code>
	*/
	public int tileAbsX(int i){
		if(i < 0 || i >= tiles.length) return (int)center.getX();
		return (int)(center.getX()+tiles[i].getX());
	}

	/**
	* Get the absolute Y location (not offset) of the <code>i</code>th tile.
	* @param i index of tile
	* @return the absolute location of tile index <code>i</code>
	*/
	public int tileAbsY(int i){
		if(i < 0 || i >= tiles.length) return (int)center.getY();
		return (int)(center.getY()+tiles[i].getY());
	}

	/**
	 * Draw self at the specified coordinates on the specified Graphics
	 * object. <code>x</code> and <code>y</code> are tile offsets (they are
	 * multiplied by <code>Tile.SIZE_*</code>)
	 * @param g Graphics object to draw on.
	 * @param rect secton of <code>g</code> to update (?)
	 * @param x x-location to draw at
	 * @param y y-location to draw at
	 */
	public void draw(Graphics g, Rectangle rect, int x, int y){
		/* these all assume the screen's dimensions are multiples
		 * of the Tile dimensions, which I think is safe */

		/* first draw the center */
		Tile.drawTile(
			x*Tile.SIZE_X,
			y*Tile.SIZE_Y,
			center.color, g, rect);

		//System.out.println("Tetrad.Draw @ (" + x + ", " + y + ") on " + rect);

		/* then the extremities */
		for(int i = 0; i < tiles.length; i++){
			Tile.drawTile(
				(int)(x+tiles[i].getX())*Tile.SIZE_X,
				(int)(y+tiles[i].getY())*Tile.SIZE_Y,
				tiles[i].color, g, rect);
		}
	}

	/**
	 * Draw self on the specified Graphics object.
	 * @param g Graphics object to draw on.
	 * @param rect secton of <code>g</code> to update (?)
	 */
	public void draw(Graphics g, Rectangle rect){
		draw(g, rect, (int)center.getX(), (int)center.getY());
	}

	/**
	 * Return a random kind of Tetrad with a random orientation, centered
	 * on (0, 0).
	 */
	public static Tetrad randomTetrad(){
		return randomTetrad(new Point(0, 0));
	}

	/**
	 * Return a random kind of Tetrad with a random orientation, centered
	 * on the given point.
	 * @param p point on which to center new Tetrad
	 */
	public static Tetrad randomTetrad(Point p){
		/* 'r' is our tersely-named global Random object */
		if(r == null){
			Debug.p("Creating new Random object. (This should only happen once per session.)");
			r = new Random();
		}

		/* pick a random tetrad */
		Tetrad t;
		int rand = r.nextInt(7);

		Debug.p("randomTetrad got " + rand + " as random Tetrad number.");
		switch(rand){
		case 0:
			t = new TetradLeftL(p);
		break;
		case 1:
			t = new TetradLine(p);
		break;
		case 2:
			t = new TetradRightL(p);
		break;
		case 3:
			t = new TetradS(p);
		break;
		case 4:
			t = new TetradSquare(p);
		break;
		case 5:
			t = new TetradT(p);
		break;
		case 6:
		default:
			t = new TetradZ(p);
		break;
		}

		/* rotate it a random number of times */
		int n = r.nextInt(4);
		Debug.p("randomTetrad got " + n + " as random rotation number.");
		for(int i = 0; i < n; i++)
			t.rotateCW();

		return t;
	}

}

