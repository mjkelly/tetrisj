/*-----------------------------------------------------------------
* Tile.java -- A tetris tile. It's just a point with color. And not even a real
* color, at that, because we'll eventually use graphics to print 'em.
*
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Fri Dec 10 23:00:44 PST 2004
-----------------------------------------------------------------*/
package TetrisJ;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;

public class Tile extends Point {
	/* color codes for the 'color' attribute */
	public final static int NOTHING	= 0; // default value
	public final static int BLUE	= 1; // TetradSquare
	public final static int GRAY	= 2; // TetradLine
	public final static int GREEN	= 3; // TetradS
	public final static int MAGENTA	= 4; // TetradT
	public final static int RED		= 5; // TetradZ
	public final static int CYAN	= 6; // TetradRightL
	public final static int YELLOW	= 7; // TetradLeftL
	public final static int WHITE	= 8; // even if we don't use it, we've gotta have white

	public final static int GREY	= 2; // alternate spelling

	public final static int SIZE_X	= 25;
	public final static int SIZE_Y	= 25;

	public final static int COLOR_MIN	= 0;
	public final static int COLOR_MAX	= 8;

	/* contains color code for this piece */
	/* no point in having accessors */
	public int color;

	/* override all of Point's constructors */
	public Tile(){
		super();
		this.color = GRAY;
	}

	public Tile(int x, int y){
		super(x, y);
		this.color = GRAY;
	}

	public Tile(Point p){
		super(p);
		this.color = GRAY;
	}

	/* copy constructor */
	public Tile(Tile p){
		super(p);
		this.color = p.color;
	}

	/* this is something Point _SHOULD_ have! (getX() and getY() return
	 * doubles, but they have to be cast back to ints manually before you can
	 * feed them to their own constructor!) */
	public Tile(double x, double y){
		super((int)x, (int)y);
		this.color = GRAY;
	}

	/* a constructor unique to Tile */
	public Tile(Point p, int c){
		super(p);

		if(c < COLOR_MIN) c = COLOR_MIN;
		else if(c > COLOR_MAX) c = COLOR_MAX;
		this.color = c;
	}

	/* convert an int representing one of the supported Tile colors to an
	 * actual, y'know, Color. */
	public static Color intToColor(int c){
		switch(c){
			case BLUE:
				return Color.BLUE;
			case GRAY:
				return Color.LIGHT_GRAY;
			case GREEN:
				return Color.GREEN;
			case MAGENTA:
				return Color.MAGENTA;
			case RED:
				return Color.RED;
			case CYAN:
				return Color.CYAN;
			case YELLOW:
				return Color.YELLOW;
			case WHITE:
					return Color.WHITE;
			case NOTHING:
			default:
				return null;
		}
	}

	/* an instance method version of intToColor() */
	public Color toColor(){
		return intToColor(this.color);
	}

	/* draw a tile of the specified color in the specified place on a specified
	 * Graphics object. x and y are PIXEL values.*/
	public static void drawTile(int x, int y, int color, Graphics g, Rectangle rect){
		//g.setColor(Color.RED);
		//g.fillRect(0, 0, 5, 5);
		if(x+SIZE_X <= rect.width && y+SIZE_Y <= rect.height
				&& x >= 0 && y >= 0){
			//System.out.println("Tile.drawTile @ (" + x + ", " + y + ") on " + rect);
			Color c = intToColor(color);
			/* if we can't convert the color, we can't draw this tile. abort. */
			if(c == null) return;
			g.setColor(c);

			g.fill3DRect(x, y, SIZE_X, SIZE_Y, true);
		}
		else{
			System.err.println("TetrisJ Error: Tile.drawTile() failed: out of bounds @ (" + x + ", " + y + ") on " + rect);
		}
	}
}

