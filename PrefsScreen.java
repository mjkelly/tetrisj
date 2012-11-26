/*-----------------------------------------------------------------
* PrefsScreen.java -- Show some configration options, write changes to an XML
* file.
*
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Thu Dec 30 18:21:11 PST 2004
-----------------------------------------------------------------*/
package TetrisJ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/* a class to represent a preferences screen. */
public class PrefsScreen extends JFrame implements WindowListener, KeyListener, FocusListener{
	/* the main screen; used to get Preference objects, gamestate, etc */
	TetrisJ mainScreen;

	JCheckBox	preview_box, grid_box;
	JButton		close_button, killscores_button;
	JRadioButton mode_0, mode_1, mode_2;
	JTextField keyField_left, keyField_right, keyField_rotate_cw,
			   keyField_rotate_ccw, keyField_down, keyField_drop,
			   keyField_pause;

	int key_left, key_right, key_rotate_cw, key_rotate_ccw, key_down, key_drop,
		key_pause;

	public static void displayPrefs(TetrisJ caller){
		PrefsScreen prefs = new PrefsScreen(caller);

		prefs.pack();
		prefs.setVisible(true);
	}

	public PrefsScreen(TetrisJ mainScreen){
		super("Preferences");
		this.mainScreen = mainScreen;

		mainScreen.menu_new.setEnabled(false);
		mainScreen.menu_scores.setEnabled(false);
		mainScreen.menu_prefs.setEnabled(false);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);

		Container pane = getContentPane();
		//pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		SpringLayout spring = new SpringLayout();
		pane.setLayout(spring);

		/* create and place a center box to hold all the toggles */
		Box center = new Box(BoxLayout.PAGE_AXIS);
		pane.add(center);
		spring.putConstraint(SpringLayout.NORTH, center, 10, SpringLayout.NORTH, pane);
		spring.putConstraint(SpringLayout.WEST, center, 5, SpringLayout.WEST, pane);

		/* now create buttons, etc, and add them to the center */
		center.add(new JLabel("Game Options:"));

		preview_box = new JCheckBox("Preview next piece (15% score bonus if OFF).");
		preview_box.setSelected(mainScreen.prefs.getBoolean("preview", true));

		grid_box = new JCheckBox("Display grid on playing screen.");
		grid_box.setSelected(mainScreen.prefs.getBoolean("grid", false));

		center.add(preview_box);
		center.add(grid_box);

		center.add(Box.createVerticalStrut(15));
		center.add(new JLabel("Game Mode:"));
		mode_0 = new JRadioButton("Normal");
		mode_1 = new JRadioButton("Sprint");
		mode_2 = new JRadioButton("Ultra");

		/* until Sprint and Ultra modes are implemented... */
		mode_0.setSelected(true);
		mode_1.setEnabled(false);
		mode_2.setEnabled(false);
		/*
		mode_0.setSelected(mainScreen.prefs.getInt("game_mode", 0) == 0);
		mode_1.setSelected(mainScreen.prefs.getInt("game_mode", 0) == 1);
		mode_2.setSelected(mainScreen.prefs.getInt("game_mode", 0) == 2);
		*/

		ButtonGroup group = new ButtonGroup();
		group.add(mode_0);
		group.add(mode_1);
		group.add(mode_2);

		center.add(mode_0);
		center.add(mode_1);
		center.add(mode_2);

		/* whee... */
		center.add(Box.createVerticalStrut(15));
		center.add(new JLabel("Keys:"));

		key_left = mainScreen.prefs.getInt("key_left", TetrisJ.KEY_LEFT);
		key_right = mainScreen.prefs.getInt("key_right", TetrisJ.KEY_RIGHT);
		key_rotate_cw = mainScreen.prefs.getInt("key_rotate_cw", TetrisJ.KEY_ROTATE_CW);
		key_rotate_ccw = mainScreen.prefs.getInt("key_rotate_ccw", TetrisJ.KEY_ROTATE_CCW);
		key_down = mainScreen.prefs.getInt("key_down", TetrisJ.KEY_DOWN);
		key_drop = mainScreen.prefs.getInt("key_drop", TetrisJ.KEY_DROP);
		key_pause = mainScreen.prefs.getInt("key_pause", TetrisJ.KEY_PAUSE);

		keyField_left = new JTextField( KeyEvent.getKeyText(key_left) );
		keyField_right = new JTextField( KeyEvent.getKeyText(key_right) );
		keyField_rotate_cw = new JTextField( KeyEvent.getKeyText(key_rotate_cw) );
		keyField_rotate_ccw = new JTextField( KeyEvent.getKeyText(key_rotate_ccw) );
		keyField_down = new JTextField( KeyEvent.getKeyText(key_down) );
		keyField_drop = new JTextField( KeyEvent.getKeyText(key_drop) );
		keyField_pause = new JTextField( KeyEvent.getKeyText(key_pause) );

		center.add(newKeyField(keyField_left, "Move Left"));
		center.add(newKeyField(keyField_right, "Move Right"));
		center.add(newKeyField(keyField_rotate_cw, "Rotate Clockwise"));
		center.add(newKeyField(keyField_rotate_ccw, "Rotate Counterclockwise"));
		center.add(newKeyField(keyField_down, "Move Down"));
		center.add(newKeyField(keyField_drop, "Drop"));
		center.add(newKeyField(keyField_pause, "Pause/Unpause"));

		center.add(Box.createVerticalStrut(15));
		killscores_button = new JButton("Clear High Scores");
		killscores_button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if( JOptionPane.showConfirmDialog(null, "Really delete high scores?",
							"Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
						HighScores.clearScores();
					}

					killscores_button.setEnabled((new File(TetrisJ.SCORE_FILE)).exists());
				}
		});
		killscores_button.setEnabled((new File(TetrisJ.SCORE_FILE)).exists());
		center.add(killscores_button);
		center.add(Box.createVerticalStrut(15));

		/* add and place the close button */
		close_button = new JButton("Done");
		close_button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					shutDown();
				}
		});

		pane.add(close_button, BorderLayout.SOUTH);
		spring.putConstraint(SpringLayout.NORTH, close_button, 10, SpringLayout.SOUTH, center);
		spring.putConstraint(SpringLayout.EAST, close_button, -5, SpringLayout.EAST, pane);

		/* place the lower-right corner of the window */
		spring.putConstraint(SpringLayout.SOUTH, pane, 10, SpringLayout.SOUTH, close_button);
		spring.putConstraint(SpringLayout.EAST, pane, 10, SpringLayout.EAST, center);

		/* can't resize SpringLayout things, apparently */
		setResizable(false);
	}

	/* close the prefs window, reactivate a few men items, and save prefs to
	 * disk */
	private void shutDown(){
		mainScreen.menu_new.setEnabled(true);
		mainScreen.menu_scores.setEnabled(true);
		mainScreen.menu_prefs.setEnabled(true);

		mainScreen.prefs.putBoolean("preview", preview_box.isSelected());
		mainScreen.prefs.putBoolean("grid", grid_box.isSelected());

		int mode;
		if(mode_1.isSelected())
			mode = 1;
		else if(mode_2.isSelected())
			mode = 2;
		else
			mode = 0;
		mainScreen.prefs.putInt("game_mode", mode);

		mainScreen.prefs.putInt("key_left", key_left);
		mainScreen.prefs.putInt("key_right", key_right);
		mainScreen.prefs.putInt("key_rotate_cw", key_rotate_cw);
		mainScreen.prefs.putInt("key_rotate_ccw", key_rotate_ccw);
		mainScreen.prefs.putInt("key_down", key_down);
		mainScreen.prefs.putInt("key_drop", key_drop);
		mainScreen.prefs.putInt("key_pause", key_pause);

		/* save prefs to disk */
		mainScreen.savePrefs(mainScreen.prefs);

		/* refresh screen objects in case changes affect the display */
		mainScreen.repaintScreen();

		this.setVisible(false);
		this.dispose();
	}

	/* sets up a labelled JTextField to be used to reassign keys. */
	private Container newKeyField(JTextField text, String labelText){
		Container c = new Container();
		c.setLayout(new GridLayout(1, 2, 5, 5));

		JLabel label = new JLabel(labelText);
		label.setHorizontalAlignment(JTextField.RIGHT);
		c.add(label);
		c.add(text);

		text.addKeyListener(this);

		text.setEditable(false);
		text.addFocusListener(this);
		text.setFont(new Font("SansSerif", Font.PLAIN, 12));

		return c;
	}

	/* for WindowListener: */
	public void windowOpened(WindowEvent e){}
	public void windowClosing(WindowEvent e){ shutDown(); }
	public void windowClosed(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}

	/* for KeyListener: */
	public void keyPressed(KeyEvent e){
		Object source = e.getSource();
		int[] keys = {key_left, key_right, key_rotate_cw, key_rotate_ccw,
			key_down, key_drop, key_pause};

		if(source == keyField_left)
			key_left = setKey(keyField_left, e.getKeyCode(), keys, 0);
		else if(source == keyField_right)
			key_right = setKey(keyField_right, e.getKeyCode(), keys, 1);
		else if(source == keyField_rotate_cw)
			key_rotate_cw = setKey(keyField_rotate_cw, e.getKeyCode(), keys, 2);
		else if(source == keyField_rotate_ccw)
			key_rotate_ccw = setKey(keyField_rotate_ccw, e.getKeyCode(), keys, 3);
		else if(source == keyField_down)
			key_down = setKey(keyField_down, e.getKeyCode(), keys, 4);
		else if(source == keyField_drop)
			key_drop = setKey(keyField_drop, e.getKeyCode(), keys, 5);
		else if(source == keyField_pause)
			key_pause = setKey(keyField_pause, e.getKeyCode(), keys, 6);
	}
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}

	/* given a text field to update, a new key code, a list of old codes, and
	 * the index of the list that the code we're changing corresponds to:
	 * updates text field and returns new code IF the code is not already in
	 * the list (except at the index we were given) */
	public int setKey(JTextField text, int newCode, int[] oldCodes, int index){
		boolean exists = false;
		/* check if newCode is in oldCodes[], skipping oldCodes[index] */
		for(int i = 0; i < oldCodes.length; i++){
			if(i == index) continue;
			if(oldCodes[i] == newCode){
				exists = true;
				break;
			}
		}

		if(!exists){
			text.setText(KeyEvent.getKeyText(newCode));
			oldCodes[index] = newCode;
			text.transferFocusUpCycle();
			return newCode;
		}
		else{
			return oldCodes[index];
		}
	}

	/* for FocusListener: */
	public void focusGained(FocusEvent e){
		JTextField text = (JTextField)e.getSource();
		text.setFont(new Font("SansSerif", Font.BOLD, 12));
	}
	public void focusLost(FocusEvent e){
		JTextField text = (JTextField)e.getSource();
		text.setFont(new Font("SansSerif", Font.PLAIN, 12));
	}

}


