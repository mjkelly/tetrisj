/*-----------------------------------------------------------------
* PreviewPane.java -- A small window into the future...
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Wed Dec 29 23:54:48 PST 2004
-----------------------------------------------------------------*/
package TetrisJ;

import javax.swing.*;
import java.awt.*;

public class PreviewPane extends JPanel{
	private GameScreen mainScreen;
	private int rows, cols;

	/* an easy way to turn the preview pane on and off */
	private boolean on;

	/* create a new PreviewPane of the specified size, linked to the given GameScreen */
	/* (it will display the Tetrad mainScreen.next) */
	public PreviewPane(){ this(5, 5); }
	public PreviewPane(int rows, int cols){
		this.mainScreen = null;
		this.rows = rows;
		this.cols = cols;
		
		Dimension d = new Dimension(cols*Tile.SIZE_X, rows*Tile.SIZE_Y);
		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		setBackground(Color.BLACK);

		on = true;
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);

		/* first we draw a grid, if desired */
		if(mainScreen.parent.prefs.getBoolean("grid", false)){
			g.setColor(Color.DARK_GRAY);
			/* horizontal lines */
			for(int y = 0; y < rows; y++)
				g.drawLine(0, y*Tile.SIZE_Y, cols*Tile.SIZE_X, y*Tile.SIZE_Y);

			/* vertical lines */
			for(int x = 0; x < cols; x++)
				g.drawLine(x*Tile.SIZE_X, 0, x*Tile.SIZE_X, rows*Tile.SIZE_Y);
		}

		if(on && mainScreen != null && mainScreen.next != null){
			mainScreen.next.draw(g, getBounds(), cols/2, rows/2);
		}
		else{
			/* if we're off, not connected, or there's no next piece, cross out
			 * the screen, a la Gnometris */
			g.setColor(Color.WHITE);
			g.drawLine(0, 0, cols*Tile.SIZE_X - 1, rows*Tile.SIZE_Y - 1);
			g.drawLine(0, rows*Tile.SIZE_Y - 1, cols*Tile.SIZE_X - 1, 0);
		}
	}

	/* DO NOT USE THIS! Use GameScreen.connectToPreview() instead, which calls this. */
	public void connectToGame(GameScreen scr){
		mainScreen = scr;
	}

	public void turnOn(){ on = true; }
	public void turnOn(boolean b){ on = b; }
	public void turnOff(){ on = false; }
	public boolean isOn(){ return on; }
}

