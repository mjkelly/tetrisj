/*-----------------------------------------------------------------
* HighScoreWindow.java -- Display high scores.
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Fri Dec 31 03:16:53 PST 2004
-----------------------------------------------------------------*/
package TetrisJ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class HighScoreWindow extends JFrame implements WindowListener{

	/** parent window */
	public TetrisJ mainScreen;

	/**
	* Create a new high scores window with no score highlighted.
	* @param parent the parent TetrisJ window
	*/
	public HighScoreWindow(TetrisJ parent){
		this(parent, -1);
	}

	/**
	* Create a new high scores window with the score at index
	* <code>hilight</code> (1-based) highlighted.
	* @param parent the parent TetrisJ window
	* @param highlight 1-based index of score to highlight
	*/
	public HighScoreWindow(TetrisJ parent, int hilight){
		super("High Scores");
		mainScreen = parent;
		Container pane = getContentPane();
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);

		mainScreen.menu_new.setEnabled(false);
		mainScreen.menu_scores.setEnabled(false);
		mainScreen.menu_prefs.setEnabled(false);

		Debug.p("new HighScoreWindow, highlighting " + hilight);

		//pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		//pane.setLayout(new GridLayout(6, 11, 5, 2));
		//$ane.setLayout(new SpringLayout());
		pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));

		Score[] scores = HighScores.readScores();

		/*
		pane.add(new JLabel("Name"));
		pane.add(new JLabel("Score"));
		pane.add(new JLabel("Lines"));
		pane.add(new JLabel("Level"));
		pane.add(new JLabel("Time"));
		pane.add(new JLabel("When"));
		*/

		/* we use a bunch of vertical boxes wrapped by a horizontal box to make
		 * a table. this is very long and very ugly. I'm sure there's a better
		 * way, but I haven't found it. tables are designed to be manipulated,
		 * GridLayouts have uniform height/width... */
		Box numBox = new Box(BoxLayout.PAGE_AXIS);
		Box nameBox = new Box(BoxLayout.PAGE_AXIS);
		Box scoreBox = new Box(BoxLayout.PAGE_AXIS);
		Box lineBox = new Box(BoxLayout.PAGE_AXIS);
		Box levelBox = new Box(BoxLayout.PAGE_AXIS);
		Box timeBox = new Box(BoxLayout.PAGE_AXIS);
		Box whenBox = new Box(BoxLayout.PAGE_AXIS);

		/* create the titles */
		Font bold = new Font("SansSerif", Font.BOLD, 12);
		Font plain = new Font("SansSerif", Font.PLAIN, 12);

		numBox.add(Box.createVerticalStrut(5));
		numBox.add(newJLabelWithFont("#", bold));
		numBox.add(Box.createVerticalStrut(10));

		nameBox.add(Box.createVerticalStrut(5));
		nameBox.add(newJLabelWithFont("Name", bold));
		nameBox.add(Box.createVerticalStrut(10));

		scoreBox.add(Box.createVerticalStrut(5));
		scoreBox.add(newJLabelWithFont("Score", bold));
		scoreBox.add(Box.createVerticalStrut(10));

		lineBox.add(Box.createVerticalStrut(5));
		lineBox.add(newJLabelWithFont("Lines", bold));
		lineBox.add(Box.createVerticalStrut(10));

		levelBox.add(Box.createVerticalStrut(5));
		levelBox.add(newJLabelWithFont("Level", bold));
		levelBox.add(Box.createVerticalStrut(10));

		timeBox.add(Box.createVerticalStrut(5));
		timeBox.add(newJLabelWithFont("Time", bold));
		timeBox.add(Box.createVerticalStrut(10));

		whenBox.add(Box.createVerticalStrut(5));
		whenBox.add(newJLabelWithFont("When", bold));
		whenBox.add(Box.createVerticalStrut(10));

		SimpleDateFormat df = new SimpleDateFormat("EEE MMM d h:mm a yyyy");
		Color curColor = Color.BLACK;

		if(scores != null){
			for(int i = 0; i < scores.length && scores[i] != null; i++){
				/* keep this code just in case we go back to a more traditional
				 * GridLayout-type thing
				pane.add(new JLabel(scores[i].name));
				pane.add(new JLabel(Long.toString(scores[i].score)));
				pane.add(new JLabel(Integer.toString(scores[i].lines)));
				pane.add(new JLabel(Integer.toString(scores[i].level)));
				pane.add(new JLabel(TetrisJ.displayMilliSecs(scores[i].time * 1000)));
				pane.add(new JLabel((new Date(scores[i].finished * 1000)).toString()));
				*/

				/*
				nameBox.add(new JLabel(scores[i].name));
				scoreBox.add(new JLabel(Long.toString(scores[i].score)));
				lineBox.add(new JLabel(Integer.toString(scores[i].lines)));
				levelBox.add(new JLabel(Integer.toString(scores[i].level)));
				timeBox.add(new JLabel(TetrisJ.displayMilliSecs(scores[i].time * 1000)));
				whenBox.add(new JLabel((new Date(scores[i].finished * 1000)).toString()));
				*/

				/* handle hilighting */
				if(hilight-1 == i)
					curColor = Color.RED;
				else
					curColor = Color.BLACK;

				numBox.add(newJLabelWithFontAndColor(Integer.toString(i+1), plain, curColor));
				nameBox.add(newJLabelWithFontAndColor(scores[i].name, plain, curColor));
				scoreBox.add(newJLabelWithFontAndColor(Long.toString(scores[i].score), plain, curColor));
				lineBox.add(newJLabelWithFontAndColor(Integer.toString(scores[i].lines), plain, curColor));
				levelBox.add(newJLabelWithFontAndColor(Integer.toString(scores[i].level), plain, curColor));
				timeBox.add(newJLabelWithFontAndColor(TetrisJ.displayMilliSecs(scores[i].time * 1000), plain, curColor));
				whenBox.add(newJLabelWithFontAndColor(df.format(new Date(scores[i].finished * 1000)), plain, curColor));

			}
		}

		numBox.add(Box.createVerticalStrut(5));
		nameBox.add(Box.createVerticalStrut(5));
		scoreBox.add(Box.createVerticalStrut(5));
		lineBox.add(Box.createVerticalStrut(5));
		levelBox.add(Box.createVerticalStrut(5));
		timeBox.add(Box.createVerticalStrut(5));
		whenBox.add(Box.createVerticalStrut(5));

		/* add each of the vertical boxes to the horizontal box, with spacing */
		pane.add(Box.createHorizontalStrut(10));
		pane.add(numBox);
		pane.add(Box.createHorizontalStrut(10));
		pane.add(nameBox);
		pane.add(Box.createHorizontalStrut(10));
		pane.add(scoreBox);
		pane.add(Box.createHorizontalStrut(10));
		pane.add(lineBox);
		pane.add(Box.createHorizontalStrut(10));
		pane.add(levelBox);
		pane.add(Box.createHorizontalStrut(10));
		pane.add(timeBox);
		pane.add(Box.createHorizontalStrut(10));
		pane.add(whenBox);
		pane.add(Box.createHorizontalStrut(10));

		/* where to add the button? this is a terrible kluge. :( */
		JButton close_button = new JButton("Done");
		close_button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				shutDown();
			}
		});

		pane.add(close_button);

		pack();
		setVisible(true);
	}

	/* return a new JLabel with the specified Font */
	private static JLabel newJLabelWithFont(String text, Font font){
		return newJLabelWithFontAndColor(text, font, Color.BLACK);
		/*
		JLabel l = new JLabel(text);
		l.setFont(font);
		return l;
		*/
	}

	/* return a new JLabel with the specified Font and Color */
	/* this is a downright Objective-C-ish name... :-/ */
	private static JLabel newJLabelWithFontAndColor(String text, Font font, Color color){
		JLabel l = new JLabel(text);
		l.setFont(font);
		l.setForeground(color);
		return l;
	}

	/* handle the closing of the window; reactive menu items, etc */
	private void shutDown(){
		mainScreen.menu_new.setEnabled(true);
		mainScreen.menu_scores.setEnabled(true);
		mainScreen.menu_prefs.setEnabled(true);

		this.setVisible(false);
		this.dispose();
	}

	/* for WindowListener: */
	public void windowOpened(WindowEvent e){}
	public void windowClosing(WindowEvent e){ shutDown(); }
	public void windowClosed(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
}

