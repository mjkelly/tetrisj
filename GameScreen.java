/*-----------------------------------------------------------------
* GameScreen.java -- The playing field.
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Sun Mar 13 21:25:18 PST 2005
-----------------------------------------------------------------*/
package TetrisJ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
* A game screen, containing the locations of all the dead pieces and the
* currently-moving piece. Also keeps track of the next pice to be spawned.
*/
public class GameScreen extends JPanel {
	/** existing "dead" pieces */
	public int[][] board;
	/** the moving, "live" piece */
	public Tetrad piece;
	/** the next piece that will be spawned, for the preview window. REAL
	 * MEN don't use the preview window! ;) */
	public Tetrad next;

	/** The "next piece" preview panel. */
	public PreviewPane preview;

	/** int to indicate gamestate, which should be one of
	 * <code>STOP</code>, <code>PLAY</code>, or <code>PAUSE</code>. */
	public int state;
	/** No game is in progress. */
	public final static int STOP = 0;
	/** A game is in progress. */
	public final static int PLAY = 1;
	/** A game is in progress, but it's paused. */
	public final static int PAUSE = 2;
	/** If the player has lost, and we should print "Game Over". Only
	 * matters if <code>state == STOP</code>. */
	boolean lose;

	/** When the last contiguous block of unpaused playing time began (in
	 * milliseconds). Use <code>elapsedMs</code> to get total playing time.
	 * */
	public long start_ms;
	/** How many milliseconds have elapsed on all previous game periods
	 * (before <code>start_ms</code>). Use <code>elapsedMs</code> to get
	 * total playing time. */
	public long elapsed_ms;

	/** X-size of playing field, in tetrad squares. */
	private int board_x;
	/** Y-size of playing field, in tetrad squares. */
	private int board_y;

	/** The parent object. */
	TetrisJ parent;

	/**
	* Create a new game screen with the given X and Y sizes.
	* @param x X-size of new screen
	* @param y Y-size of new screen
	*/
	public GameScreen(int x, int y, TetrisJ p){
		board_x = x;
		board_y = y;
		parent = p;

		/* initialized, but obviously a bad value */
		start_ms = 0;
		elapsed_ms = 0;

		piece = next = null;
		board = null;
		lose  = false;

		state = STOP;

		/* to layout manager: do NOT bloody change the size! */
		Dimension d = new Dimension(x*Tile.SIZE_X, y*Tile.SIZE_Y);
		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		setBackground(Color.BLACK);
	}

	/**
	* Paint the current screen. Called by Swing.
	*/
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Color c;

		/* first we draw a grid, if desired */
		if(parent.prefs.getBoolean("grid", false)){
			g.setColor(Color.DARK_GRAY);
			/* horizontal lines */
			for(int y = 0; y < board_y; y++)
				g.drawLine(0, y*Tile.SIZE_Y, board_x*Tile.SIZE_X, y*Tile.SIZE_Y);

			/* vertical lines */
			for(int x = 0; x < board_x; x++)
				g.drawLine(x*Tile.SIZE_X, 0, x*Tile.SIZE_X, board_y*Tile.SIZE_Y);
		}

		/* now we draw the existing pieces */
		if(board != null){
			for(int y = 0; y < board_y; y++){
				for(int x = 0; x < board_x; x++){
					Tile.drawTile(x*Tile.SIZE_X, y*Tile.SIZE_Y, board[y][x], g, getBounds());
				}
			}
		}

		/* now we draw the piece in motion, if any */
		if(piece != null) piece.draw(g, getBounds());

		/* if we're paused, indicate that */
		if(state == PAUSE){
			g.setFont(new Font("SanSerif", Font.BOLD, 24));
			g.setColor(Color.WHITE);
			g.drawString("Paused", getWidth()/4, getHeight()/2);
		}
		else if(state == STOP && lose){
			g.setFont(new Font("SanSerif", Font.BOLD, 24));
			g.setColor(Color.WHITE);
			g.drawString("Game Over", getWidth()/4, getHeight()/2);
		}
	}

	/**
	* Rotate the live piece clockwise.
	* @return true if there is a live piece and it can rotate, false
	* otherwise
	*/
	public synchronized boolean rotateCW(){
		if(piece != null && canRotateCW()){
			piece.rotateCW();
			this.repaint();
			return true;
		}
		else
			return false;
	}
	
	/**
	* Rotate the live piece counter-clockwise.
	* @return true if there is a live piece and it can rotate, false
	* otherwise
	*/
	public synchronized boolean rotateCCW(){
		if(piece != null && canRotateCCW()){
			piece.rotateCCW();
			this.repaint();
			return true;
		}
		else
			return false;
	}

	/**
	* Move the live piece one square down.
	* @return true if there is a live piece and it has empty space below
	* it, false otherwise
	*/
	public synchronized boolean down(){
		if(piece != null && canFall()){
			piece.center.translate(0, 1);
			this.repaint();
			return true;
		}
		else
			return false;
	}

	/**
	* Drop the live piece until it hits dead squares.
	* @return how many squares dropped; 0 if there is no live piece
	*/
	public synchronized int drop(){
		/* TODO: replace this with a more efficient implementation? */
		if(piece == null) return 0;
		int fall = 0;
		while(canFall()){
			piece.center.translate(0, 1);
			fall++;
		}
		this.repaint();
		return fall;
	}

	/**
	* Move the live piece one square to the left.
	* @return true if there is a live piece and it can be moved, false
	* otherwise.
	*/
	public synchronized boolean left(){
		if(piece != null && canMoveLeft()){
			piece.center.translate(-1, 0);
			this.repaint();
			return true;
		}
		else
			return false;
	}

	/**
	* Move the live piece one square to the right.
	* @return true if there is a live piece and it can be moved, false
	* otherwise.
	*/
	public synchronized boolean right(){
		if(piece != null && canMoveRight()){
			piece.center.translate(1, 0);
			this.repaint();
			return true;
		}
		else
			return false;
	}

	/**
	* Check of the live piece can fall.
	* @return true if it can, false if it can't
	*/
	public boolean canFall(){
		return canTranslate(0, 1);
	}
	/**
	* Check of the live piece can move left.
	* @return true if it can, false if it can't
	*/
	public boolean canMoveLeft(){
		return canTranslate(-1, 0);
	}
	/**
	* Check of the live piece can move right.
	* @return true if it can, false if it can't
	*/
	public boolean canMoveRight(){
		return canTranslate(1, 0);
	}

	/**
	* Check if the live piece can be translated by the indicated amounts
	* without running into the edge of the screen or dead pieces.
	* @param dx X translation to check
	* @param dy Y translation to check
	* @return true if it can, false if it can't
	*/
	private boolean canTranslate(int dx, int dy){
		return canTranslate(dx, dy, piece);
	}

	/**
	* Check if the given <code>Tetrad</code> can be translated by the
	* indicated amounts without running into the edge of the screen or dead
	* pieces.
	* @param dx X translation to check
	* @param dy Y translation to check
	* @param aPiece Tetrad to check.
	* @return true if it can, false if it can't
	*/
	private boolean canTranslate(int dx, int dy, Tetrad aPiece){
		if(aPiece == null) return false;

		if(!validSquare((int)aPiece.center.getX()+dx, (int)aPiece.center.getY()+dy))
			return false;

		for(int i = 0; i < aPiece.tiles.length; i++){
			if(!validSquare(aPiece.tileAbsX(i)+dx, aPiece.tileAbsY(i)+dy))
				return false;
		}
		return true;

	}

	/**
	* Check if this square can be occupied. Specifically, if it is on the
	* board and not already occupied by a "dead" square
	* @param x x-coordinate to check
	* @param y y-coordinate to check
	* @return true if the square is "valid", false if it isn't
	*/
	private boolean validSquare(int x, int y){
			return (y < board_y && x < board_x && y >= 0 && x >= 0
				&& board[y][x] == Tile.NOTHING);
	}

	/**
	* check if the live piece can rotate clockwise without ending up on top
	* of dead pieces. Does not check "intermediate" squares.
	*/
	private boolean canRotateCW(){
		int x, y;
		for(int i = 0; i < piece.tiles.length; i++){
			/* this part emulates the rotation, as in Tetrad.rotateTileCW() */
			x = (int)(piece.center.getX() - piece.tiles[i].getY());
			y = (int)(piece.center.getY() + piece.tiles[i].getX());

			if(!validSquare(x, y))
				return false;
		}
		return true;
	}

	/**
	* check if the live piece can rotate counter-clockwise without ending
	* up on top of dead pieces. Does not check "intermediate" squares.
	*/
	private boolean canRotateCCW(){
		int x, y;
		for(int i = 0; i < piece.tiles.length; i++){
			/* this part emulates the rotation, as in Tetrad.rotateTileCCW() */
			x = (int)(piece.center.getX() + piece.tiles[i].getY());
			y = (int)(piece.center.getY() - piece.tiles[i].getX());

			if(!validSquare(x, y))
				return false;
		}
		return true;
	}

	/**
	* Spawn a new live piece at the top of the screen. If the new piece
	* would cover existing dead squares, the game is ended via an
	* <code>EndGameException</code>.
	* @see EndGameException
	*/
	public synchronized void spawn(){
		if(next == null){
			Debug.p("spawn(): next == null. Creating at (" + board_x/2 + ", " + 2 + ") (This should be the first drop of the game.)");
			next  = Tetrad.randomTetrad(new Point(board_x/2, 2));
		}

		/* if there's no room for this new piece, remove it and stop the game */
		if(!canTranslate(0, 0, next)){
			Debug.p("spawn(): No room for next piece! Throwing EndGameException.");
			piece = null;
			preview.repaint();
			repaint();
			throw new EndGameException("Ran out of room.");
		}
		/* otherwise, we're good to go */
		else{
			Debug.p("spawn(): Successfully spawned new piece at (" + board_x/2 + ", " +  2 + ")");
			piece = next;
			next  = Tetrad.randomTetrad(new Point(board_x/2, 2));
		}

		preview.repaint();
		repaint();
	}

	/**
	* Convert the live piece into dead squares, such as when it can't move
	* any further down. A new live piece is <i>not</i> automatically spawned.
	* @see spawn
	*/
	public synchronized void freeze(){
		board[(int)piece.center.getY()][(int)piece.center.getX()] = piece.center.color;
		for(int i = 0; i < piece.tiles.length; i++){
			board[piece.tileAbsY(i)][piece.tileAbsX(i)] = piece.tiles[i].color;
		}
		piece = null;
	}

	/**
	* Clear any filled rows on the game screen. Those lines are removed,
	* lines above them are shifted down, and new lines are added at the
	* top.
	* @return number of rows cleared
	*/
	public synchronized int clearRows(){
		int cleared = 0;
ROW:	for(int y = board_y-1; y >= cleared; y--){
			//System.out.print("Checking row " + y);
			for(int x = 0; x < board_x; x++){
				if(board[y][x] == Tile.NOTHING){
					continue ROW;
				}
			}
			/* if we make it here, it's a full row */
			cleared++;
			Debug.p(2, "Cleared row " + y + " (" + cleared + " so far).");

			/* move up, "falling" rows as we go */
			for(int i = y; i > 0; i--){
				board[i] = board[i-1];
			}

			/* if we cleared a row, we have to make another pass over "this" row,
			 * because everything's been shifted down by one */
			y++;
		}
		/* now add empty rows at the top */
		for(int i = 0; i < cleared; i++){
			board[i] = new int[board_x];
			//System.out.println("Adding row " + i);
		}
		repaint();
		return cleared;
	}

	/**
	* Connect the game screen to a <code>PreviewPane</code> object. This
	* object will update the preview whenever the gamestate warrants it,
	* and the preview will know the next piece
	* @param p <code>PreviewPane<code> object to connect to.
	*/
	public void connectToPreview(PreviewPane p){
		preview = p;
		preview.connectToGame(this);
	}

	/**
	* Get the number of milliseconds that have elapsed since the current
	* game began.
	* @return length of current game, in ms
	*/
	public long elapsedMs(){
		if(state == PAUSE)
			return elapsed_ms;
		else
			return System.currentTimeMillis() - start_ms + elapsed_ms;
	}

	/**
	* Get the number of full seconds (truncated toward zero) elapsed since
	* the current game began.
	* @return length of current game, in seconds
	*/
	public long elapsedSecs(){
		return elapsedMs()/1000;
	}

}
