/*-----------------------------------------------------------------
* TetrisJ.java -- Tetris in Java.
* Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Wed May  4 16:55:41 PDT 2005
-----------------------------------------------------------------*/
package TetrisJ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.prefs.*;
import java.io.*;
import java.text.NumberFormat;

public class TetrisJ extends JFrame{
	/*------------------------------------------------------ *
	 * Here are a few things people might want to change.    *
	 * ----------------------------------------------------- */
	/* names of prefs and high-score files */
	public static final String PREF_FILE = "TetrisJ-prefs.xml";
	public static final String SCORE_FILE = "TetrisJ-scores.dat";

	/* size of the playing field, in tiles */
	public final static int BOARD_X = 11;
	public final static int BOARD_Y = 20;

	/* debug messages are printed to stdout.
	 * 0 = no debug messages
	 * 1 = operational messages
	 * 2 = 1 + scoring messages
	 * 3 = 2 + key events
	 */
	public final static int DEBUG_LEVEL = 0;

	/*------------------------------------------------------ *
	 * The rest, people probably won't care about.           *
	 * ----------------------------------------------------- */
	public final static String DISPL_NAME = "TetrisJ"; //after all, it may change

	public final static int VER_MAJOR = 1;
	public final static int VER_MINOR = 0;
	public final static int VER_POINT = 4;

	/* "a" for alpha, "b" for beta, or other special things */
	public final static String VER_TAG = "-r1";

	private static final String INFO_MSG =
		  DISPL_NAME + " version " + versionString() + "\n\n"
		+ "Copyright 2004-2005 Michael Kelly\n"
		+ "<michael@michaelkelly.org>\n"
		+ "<http://www.michaelkelly.org>\n\n"

		+ "Released under the GNU GPL.\n"
		+ "See 'COPYING' file for the full license.\n\n"

		+ "Read the readme for more information.\n\n";

	/* default keys, if there's no prefs file or it's inaccessible */
	public final static int KEY_LEFT = KeyEvent.VK_LEFT,
		   KEY_RIGHT = KeyEvent.VK_RIGHT,
		   KEY_ROTATE_CW = KeyEvent.VK_R,
		   KEY_ROTATE_CCW = KeyEvent.VK_UP,
		   KEY_DOWN = KeyEvent.VK_DOWN,
		   KEY_DROP = KeyEvent.VK_SPACE,
		   KEY_PAUSE = KeyEvent.VK_P;

	/* instance variables */
	private GameScreen screen;
	private PreviewPane preview;

	public Preferences prefs;
	public Score[] highScores;

	/* a flag for controlling which mode we're in. only NORMAL is currently
	 * implemented */
	private int mode;
	private static final int MODE_NORMAL = 0, MODE_SPRINT = 1, MODE_ULTRA = 2;

	private long score;
	private int lines, level;
	private JLabel scoreTxt, linesTxt, levelTxt, timeTxt;

	/* all the menu items; used for dispatching actions from the handler */
	public JMenuItem	menu_new, menu_pause, menu_end, menu_scores, menu_quit,
						menu_prefs,
						menu_about;
	
	/* this is 'this'; it's used so inner classes have access to it */
	private TetrisJ rootWindow;

	/* a variable that tracks whether a GravityThread is running. new
	 * GravityThreads will not start while this is true. thus, this value is
	 * DESCRIPTIVE, not PRESCRIPTIVE. (the prescriptive value is screen.state)
	 * */
	private boolean threadRunning;
	/* used to synchronize thread operations (specifically those relating to
	 * checking threadRunning, where using 'this' is not appropriate) */
	private static Object threadLock;

	/* entry point to the whole program */
	public static void main(String[] args) {

		/* transfer the debug value to where it really matters */
		Debug.LEVEL = DEBUG_LEVEL;

		Debug.p("Debug log for " + DISPL_NAME + " " + versionString() + " at " + new Date() + ". Debug level = " + DEBUG_LEVEL);
		Debug.p("+=================================================================+");

		TetrisJ game = new TetrisJ();
		game.pack();
		game.setVisible(true);
	}

	/* public constructor; sets up main window, ready to be packed and made visible */
	public TetrisJ(){
		super(DISPL_NAME + " " + versionString());
		this.setBackground(Color.LIGHT_GRAY);

		/* any object will do */
		threadLock = new Object();

		Container pane = this.getContentPane();
		SpringLayout spring = new SpringLayout();
		pane.setLayout(spring);

		/* first we set up the menus */
		JMenuBar bar = new JMenuBar();

		rootWindow = this;

		/* here's an inner class to deal with handling menu selections */
		/* this is the springboard for all the menu actions */
		class MenuListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				Object src = e.getSource();

				/* because we saved all the menu-item names, we can avoid the
				 * STUPID method: matching on visible selection names, which
				 * are apt to change */
				if(src == menu_new){
					Debug.p("Pause from menu/accelerator.");
					newGame();
				}
				else if(src == menu_pause){
					Debug.p("Pause from menu/accelerator.");
					togglePause();
				}
				else if(src == menu_end){
					Debug.p("End game from menu/accelerator.");
					endGame(true);
				}
				else if(src == menu_scores){
					Debug.p("High scores window from menu/accelerator.");
					new HighScoreWindow(rootWindow);
				}
				else if(src == menu_quit){
					Debug.p("Quit from menu/accelerator.");
					System.exit(0);
				}
				else if(src == menu_prefs){
					Debug.p("Prefs window from menu/accelerator.");
					PrefsScreen.displayPrefs(rootWindow);
				}
				else if(src == menu_about){
					Debug.p("About window from menu/accelerator.");
					JOptionPane.showMessageDialog(rootWindow, INFO_MSG, "About " + DISPL_NAME, JOptionPane.INFORMATION_MESSAGE);
				}
				else{
					Debug.p("!!! Uncaught ActionEvent to TetrisJ MenuListener.");
				}
			}
		}
		MenuListener listener = new MenuListener();

		/* build the menu */
		/* ----- */
		JMenu gameMenu = new JMenu("Game");

		menu_new = gameMenu.add(makeMenuItem("New Game", listener, KeyEvent.VK_N,
			KeyEvent.VK_N, InputEvent.CTRL_MASK, true));
		menu_pause = gameMenu.add(makeMenuItem("Pause Game", listener, KeyEvent.VK_P,
			KeyEvent.VK_P, InputEvent.CTRL_MASK, false));
		menu_end = gameMenu.add(makeMenuItem("End Game", listener, KeyEvent.VK_E,
			KeyEvent.VK_E, InputEvent.CTRL_MASK, false));
		gameMenu.addSeparator();
		menu_scores = gameMenu.add(makeMenuItem("High Scores...", listener, KeyEvent.VK_H,
			KeyEvent.VK_H, InputEvent.CTRL_MASK, true));
		gameMenu.addSeparator();
		menu_quit = gameMenu.add(makeMenuItem("Quit", listener, KeyEvent.VK_Q,
			KeyEvent.VK_Q, InputEvent.CTRL_MASK, true));

		/* ----- */
		JMenu settingsMenu = new JMenu("Settings");

		menu_prefs = settingsMenu.add(makeMenuItem("Preferences...", listener, KeyEvent.VK_R,
			KeyEvent.VK_R, InputEvent.CTRL_MASK, true));

		/* ----- */
		JMenu helpMenu = new JMenu("About");

		menu_about = helpMenu.add(makeMenuItem("About " + DISPL_NAME, listener, KeyEvent.VK_A, true));

		/* add everything to the menu bar... */
		bar.add(gameMenu);
		bar.add(settingsMenu);
		bar.add(helpMenu);

		/* add the menu bar to the window... */
		setJMenuBar(bar);
		/* and we're done with the menu */

		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){ handleKey(e); }
			public void keyReleased(KeyEvent e){ }
			public void keyTyped(KeyEvent e){ handleKey(e); }
		});

		screen = new GameScreen(BOARD_X, BOARD_Y, this);
		preview = new PreviewPane();
		screen.connectToPreview(preview);

		pane.add(screen);
		pane.add(preview);

		/* the score box, within sideDisplay */
		Box scoreDisplay = new Box(BoxLayout.PAGE_AXIS);

		/* initial state; nothing that would be printed by drawStats() */
		scoreTxt = (JLabel)scoreDisplay.add(new JLabel("Score: -"));
		linesTxt = (JLabel)scoreDisplay.add(new JLabel("Lines: -"));
		levelTxt = (JLabel)scoreDisplay.add(new JLabel("Level: -"));
		timeTxt  = (JLabel)scoreDisplay.add(new JLabel("Time: -"));

		pane.add(scoreDisplay);
		
		/* | <-- screen */
		spring.putConstraint(SpringLayout.WEST, screen, 5, SpringLayout.WEST, pane);
		spring.putConstraint(SpringLayout.NORTH, screen, 5, SpringLayout.NORTH, pane);
		/* screen --> | <-- side panel */
		spring.putConstraint(SpringLayout.WEST, preview, 5, SpringLayout.EAST, screen);
		spring.putConstraint(SpringLayout.NORTH, preview, 5, SpringLayout.NORTH, pane);

		spring.putConstraint(SpringLayout.WEST, scoreDisplay, 5, SpringLayout.EAST, screen);
		spring.putConstraint(SpringLayout.NORTH, scoreDisplay, 5, SpringLayout.SOUTH, preview);

		/* fix the left edge of the window after the left edge of the preview window */
		spring.putConstraint(SpringLayout.EAST, pane, 5, SpringLayout.EAST, preview);

		/* fix the bottom of the window below the bottom of the screen */
		spring.putConstraint(SpringLayout.SOUTH, pane, 5, SpringLayout.SOUTH, screen);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		/* and now, once the window's set up, open up the preferences and high scores */
		try{
			prefs = Preferences.userNodeForPackage(TetrisJ.class);

			prefs.importPreferences(new FileInputStream(PREF_FILE));
		}
		catch(FileNotFoundException e){
			/* okay, we couldn't find the file. pump it full of values and save it. */
			initPrefs(prefs);
			savePrefs(prefs);
		}
		catch(IOException e){
			Debug.p("!!! Error loading preferences: " + e + ": " + e.getMessage());
			JOptionPane.showMessageDialog(rootWindow, "IOException: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(InvalidPreferencesFormatException e){
			Debug.p("!!! Error loading preferences: " + e + ": " + e.getMessage());
			JOptionPane.showMessageDialog(rootWindow, "InvalidPreferencesFormatException: " + e.getMessage() + ". Preferences are likely lost. Sorry.", "Error", JOptionPane.ERROR_MESSAGE);
		}

		preview.turnOn(prefs.getBoolean("preview", true));

	}

	/* start a new game */
	public synchronized void newGame(){
		Debug.p("~-=-~ NEW GAME ~-=-~");
		/* these are all conveniently initialized to 0, which is Tile.NOTHING,
		 * i.e., transparent */
		screen.board = new int[BOARD_Y][BOARD_X];

		/* clear the old "next piece" */
		screen.next = null;

		/* end any game already in progress, without a dialog */
		if(screen.state == GameScreen.PLAY){
			Debug.p("!!! Ending old game from newGame() call. The user shouldn't be able to do this.");
			endGame(false);
		}

		screen.state = GameScreen.PLAY;

		/* we don't catch EndGameException here, because we have a new board,
		 * so that won't happen. */
		screen.spawn();
		screen.elapsed_ms = 0;
		screen.start_ms = System.currentTimeMillis();

		menu_new.setEnabled(false);
		menu_pause.setEnabled(true);
		menu_end.setEnabled(true);
		menu_prefs.setEnabled(false);

		preview.turnOn(prefs.getBoolean("preview", true));

		score = 0;
		lines = 0;
		level = 1;

		drawStats();

		/* aaaand, start the gravity thread */
		(new GravityThread()).start();
		(new TimeThread()).start();

		screen.repaint();
	}

	/* stop a game in progress */
	public void endGame(boolean withDialog){
		/* this is a sigil to stop the gravity and timer threads */
		screen.state = GameScreen.STOP;

		Debug.p("endGame(). withDialog = " + withDialog);

		menu_new.setEnabled(true);
		menu_pause.setEnabled(false);
		menu_end.setEnabled(false);
		menu_prefs.setEnabled(true);

		Score[] hs = HighScores.readScores();

		/* check if this is a high score; if so, prompt the user for a name and
		 * add the entry */
		if(HighScores.isHighScore(score, lines, (int)screen.elapsedMs()/1000, hs)){
			String name = 
				JOptionPane.showInputDialog(this, "Congratulations, you have"
						+ " achieved a high score. Enter your name for posterity.",
						"High score!", JOptionPane.INFORMATION_MESSAGE);

			/* null is returned if the user hit 'cancel' */
			if(name != null){
				/* don't allow blank names */
				if(name.length() == 0) name = "Anonymous";

				Score newScore = new Score(name, score, lines, level,
						(int)screen.elapsedMs()/1000,
						System.currentTimeMillis()/1000);
				hs = HighScores.addHighScore(newScore, hs);

				HighScores.writeScores(hs);

				if(withDialog){
					/* figure out where the score we just added went, so we can
					 * higlight it */
					int index = java.util.Arrays.binarySearch(hs, newScore);
					new HighScoreWindow(this, index+1);
				}
			}
		}
		else{
			if(withDialog){
				JOptionPane.showMessageDialog(this, "Game over. You cleared " + lines
						+ " lines in " + displayMilliSecs(screen.elapsedMs()) + " for "
						+ NumberFormat.getInstance().format(score) + " points.", "Game Over",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

		/* you fail it. your skill is not enough. ;) */
		screen.lose = true;

		/* just make _absolutely sure_ everything's drawn */
		drawStats();
		screen.repaint();
	}

	/* toggle pausing of a game */
	public void togglePause(){
		if(screen.state == GameScreen.STOP){
			Debug.p("!!! Got togglePause() call while stopped. What happen?");
			return;
		}
		if(screen.state == GameScreen.PAUSE){
			/* unpause */
			Debug.p("Unpausing.");
			screen.start_ms = System.currentTimeMillis();
			(new TimeThread()).start();
			screen.state = GameScreen.PLAY;
			screen.repaint();
		}
		else{
			/* pause */
			Debug.p("Pausing.");
			screen.elapsed_ms += System.currentTimeMillis() - screen.start_ms;
			screen.state = GameScreen.PAUSE;
			screen.repaint();
		}
	}

	/* convert an int, representing seconds, into the textual representation: "min:secs" */
	public static String displayMilliSecs(long t){
		t /= 1000; // convert t to seconds
		return (t/60) + ":" + ((t%60 < 10) ? "0" : "") + (t%60);
	}

	/* handle a keypress */
	private void handleKey(KeyEvent k){
		int c = k.getKeyCode();	
		if(c == prefs.getInt("key_rotate_cw", KEY_ROTATE_CW) && screen.state == GameScreen.PLAY){
			Debug.p(3, "KeyEvent: rotate CW.");
			screen.rotateCW();
		}
		else if(c == prefs.getInt("key_rotate_ccw", KEY_ROTATE_CCW) && screen.state == GameScreen.PLAY){
			Debug.p(3, "KeyEvent: rotate CCW.");
			screen.rotateCCW();
		}
		else if(c == prefs.getInt("key_left", KEY_LEFT) && screen.state == GameScreen.PLAY){
			Debug.p(3, "KeyEvent: move left.");
			screen.left();
		}
		else if(c == prefs.getInt("key_right", KEY_RIGHT) && screen.state == GameScreen.PLAY){
			Debug.p(3, "KeyEvent: move right.");
			screen.right();
		}
		else if(c == prefs.getInt("key_down", KEY_DOWN) && screen.state == GameScreen.PLAY){
			Debug.p(3, "KeyEvent: move down.");
			scoreDown();
		}
		else if(c == prefs.getInt("key_drop", KEY_DROP) && screen.state == GameScreen.PLAY){
			Debug.p(3, "KeyEvent: drop.");
			scoreDrop();
		}
		else if(c == prefs.getInt("key_pause", KEY_PAUSE)){
			Debug.p(3, "KeyEvent: pause/unpause.");
			togglePause();
		}
	}

	/* a synchronized wrapper to move a piece down, scoring it if it hits other
	 * pieces */
	public void scoreDown(){
		synchronized(screen){
			if(!screen.down()){
				scoreNewPiece();
			}
		}
	}

	/* to be called after a new piece is added, this freezes the piece, clears
	 * rows, adds score, lines cleared, recalulates the level, and spawns a new
	 * piece */
	public void scoreNewPiece(){
		synchronized(screen){
			screen.freeze();

			int newRows = screen.clearRows();
			/* the minimum is ten points for successfully dropping a piece */
			long newScore = (long)(100 * newRows * Math.pow(2, newRows-1)) + 5;
			/* this multiplier is:
			   1.0 on level 1
			   1.2 on level 2
			   1.4 on level 3
			   1.6 on level 4
			   etc...
			 */
			/* and a 15% bonus for not using the preview screen */
			newScore *= (0.8 + level*0.2) * (preview.isOn() ? 1.0 : 1.15);

			Debug.p(2, "scoreNewPiece(): " + newScore);
			score += newScore;

			addLines(newRows);
			try{
				screen.spawn();
			}
			catch(EndGameException e){
				Debug.p("Ending game from EndGameException (screen.spawn() failed; probably tile collision).");
				endGame(true);
			}
			drawStats();
		}
	}

	/* to be called when a piece is to be dropped. handles scoring. */
	public void scoreDrop(){
		synchronized(screen){
			int newScore = screen.drop() * 1;
			/* 15% bonus for not using the preview screen */
			/* plus the level multiplier explained in scoreNewPiece() */
			newScore *= (0.8 + level*0.2) * (preview.isOn() ? 1.0 : 1.15);
			Debug.p(2, "scoreDrop(): " + newScore);
			score += newScore;
			scoreNewPiece();
		}
	}

	/* add specified number of lines to this.lines, keeping an eye out for a
	 * level change */
	public synchronized void addLines(int newLines){
		while(newLines > 0){
			this.lines++;
			newLines--;
			/* if the new number of lines cleared is a multiple of 10, we've
			 * moved to a new level (line # 10, 20, etc) */
			if(this.lines % 10 == 0)
				level++;
		}
	}

	/* draw the stats screen */
	/* there are four cases when this needs to be done:
	   1. When a piece is dropped (score changes).
	   2. When a piece is moved down (if it's frozen, score changes).
	   3. When the gravity thread moves a piece down (if it's frozen, score changes).
	   4. Every few ms, to update the time. (1/4 sec? 1/10 sec?)
	   This probably isn't the place for this list.
	*/
	public void drawStats(){
		scoreTxt.setText("Score: " + score);
		linesTxt.setText("Lines: " + lines);
		levelTxt.setText("Level: " + level);
		timeTxt.setText("Time: " + displayMilliSecs(screen.elapsedMs()));
	}

	/* make a new JMenuItem and set common attributes. */
	private static JMenuItem makeMenuItem(String label, ActionListener listener, int mnemonic, boolean enabled){
		return makeMenuItem(label, listener, mnemonic, 0, 0, enabled);
	}

	private static JMenuItem makeMenuItem(String label, ActionListener listener, int mnemonic, int accelerator, int accel_mod, boolean enabled){
		JMenuItem item = new JMenuItem(label, mnemonic);
		if(listener != null)
			item.addActionListener(listener);
		item.setEnabled(enabled);
		if(accelerator > 0)
			item.setAccelerator(KeyStroke.getKeyStroke(accelerator, accel_mod));
		return item;
	}

	/* return a version information string; suitable for printing in titles, etc */
	public static String versionString(){
		return(VER_MAJOR + "." + VER_MINOR + "." + VER_POINT + VER_TAG);
	}

	/* knowing the gamestate, calculate how many miliseconds to before dropping the piece */
	private int fallDelay(){
		/* just hard-code this... I can't think of a good formula. */
		switch(level){
			case 1: return 1000;
			case 2: return 850;
			case 3: return 700;
			case 4: return 600;
			case 5: return 500;
			case 6: return 400;
			case 7: return 350;
			case 8: return 300;
			case 9: return 250;
			case 10: return 200;
			case 11: return 180;
			case 12: return 160;
			case 13: return 140;
			case 14: return 130;
			case 15: return 120;
			case 16: return 110;
			default: return 100;
			/* these high level (>11 or so) speed progressions haven't really
			 * been tested... if you actually get that far, you're better than
			 * I am. ;) */
		}
	}

	/* initialize a Preferences object with default values for all our preferences. */
	public void initPrefs(Preferences p){
		/* this will get longer, I imagine. */
		try{
			p.clear();
		} catch(BackingStoreException e){}

		p.putBoolean("preview", true);

		p.putInt("key_left", KEY_LEFT);
		p.putInt("key_right", KEY_RIGHT);
		p.putInt("key_rotate_cw", KEY_ROTATE_CW);
		p.putInt("key_rotate_ccw", KEY_ROTATE_CCW);
		p.putInt("key_down", KEY_DOWN);
		p.putInt("key_drop", KEY_DROP);
		p.putInt("key_pause", KEY_PAUSE);

	}

	/* write our Preferences object to disk */
	public void savePrefs(Preferences p){
		synchronized(prefs){
			try{
				prefs.exportNode(new FileOutputStream(PREF_FILE));
			}
			catch(BackingStoreException e){
				Debug.p("!!! Error saving preferences: " + e + ": " + e.getMessage());
				JOptionPane.showMessageDialog(rootWindow, "Error saving preferences (BackingStoreException): " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch(IOException e){
				Debug.p("!!! Error saving preferences: " + e + ": " + e.getMessage());
				JOptionPane.showMessageDialog(rootWindow, "Error saving preferences (IOException): " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/* repaint on-screen components. Don't want to call it "repaint", to avoid confusion. */
	public void repaintScreen(){
		screen.repaint();
		preview.repaint();
	}

	/* now the separate threads as inner classes */
	private class GravityThread extends Thread{

		public void run(){
			/* prevent more than one GravityThread from running at once */
			/* this should never happen unless a the is stopped and then
			 * started again within fallDelay() ms: the thread will be
			 * sleeping, and when it checks screen.state again, it'll be
			 * GameScreen.PLAY. */
			synchronized(threadLock){
				if(threadRunning){
					Debug.p("!!! GravityThread already running! Not starting.");
					return;
				}
				else{
					Debug.p("GravityThread starting.");
				}
				threadRunning = true;
			}

			while(true){
				synchronized(threadLock){
					if(screen.state == GameScreen.STOP){
						Debug.p("GravityThread stopping (state == STOP; first check)");
						threadRunning = false;
						break;
					}
					else if(screen.state == GameScreen.PAUSE){
						try{ Thread.sleep(10); }
						catch(InterruptedException e){}
						continue;
					}
				}

				try{ Thread.sleep(fallDelay()); }
				catch(InterruptedException e){}

				synchronized(threadLock){
					if(screen.state == GameScreen.STOP){
						Debug.p("GravityThread stopping (state == STOP; second check)");
						threadRunning = false;
						break;
					}
					else if(screen.state == GameScreen.PAUSE){
						try{ Thread.sleep(10); }
						catch(InterruptedException e){}
						continue;
					}
				}

				/* scoreDown() is synchronized(screen) */
				scoreDown();
			}
		}
	}

	/* keep track of the game time elapsed; more difficult than it should be,
	 * since we pause */
	private class TimeThread extends Thread{
		public void run(){
			Debug.p("TimerThread starting.");
			while(true){
				if(screen.state == GameScreen.STOP){
					Debug.p("TimerThread stopping (state == STOP)");
					break;
				}
				try{ Thread.sleep(100); }
				catch(InterruptedException e){}
				drawStats();
			}
		}
	}
}

