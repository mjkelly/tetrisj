/*-----------------------------------------------------------------
* HighScores.java -- Keep track of Tetris high scores in an XML file.
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Fri Dec 31 00:43:47 PST 2004
-----------------------------------------------------------------*/
package TetrisJ;

import java.io.*;
import java.util.prefs.*;
import java.util.Vector;

/**
* A collection of static methods for dealing with collections of <code>Score</code> objects.
* @see Score
*/
public class HighScores {
	/** Maximum number of scores to have in a high scores list. */
	public static final int MAX_SCORES = 20;

	/** test driver -- ignore. */
	public static void main(String[] args){
		/*
		Score[] scores = {
			new Score("Michael0", 1005, 10, 2, 120, 1104529797),
			new Score("Michael1", 1002, 10, 2, 120, 1104529797),
			new Score("Michael2", 1001, 10, 2, 120, 1104529797),
			new Score("Michael3", 1004, 10, 2, 120, 1104529797),
			new Score("Michael4", 1003, 10, 2, 120, 1104529797),
		};
		writeScores(scores);
		scores = readScores();
		*/

		Score[] scores = readScores();
		scores = addHighScore(new Score("Michael5", 1003, 11, 2, 120, 1104529797), scores);
		scores = addHighScore(new Score("Michael6", 1006, 11, 2, 120, 1104529797), scores);
		scores = addHighScore(new Score("Michael6", 1007, 11, 2, 120, 1104529797), scores);
		scores = addHighScore(new Score("Michael8", 1008, 11, 2, 120, 1104529797), scores);
		scores = addHighScore(new Score("Michael9", 1009, 11, 2, 120, 1104529797), scores);
		scores = addHighScore(new Score("Michael10", 1010, 11, 2, 120, 1104529797), scores);

		for(int i = 0; i < scores.length; i++){
			System.out.println(i + ": " + scores[i]);
		}
	}

	/**
	* Read high scores from disk.
	* @return an array of scores on the disk file
	*/
	public static Score[] readScores(){
		Score[] scores = null;
		int i = 0;
		try{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(TetrisJ.SCORE_FILE));

			// the first item in the file is how many scores there are
			int l = in.readInt();
			if(l > MAX_SCORES) l = MAX_SCORES;
			scores = new Score[l];
			//System.out.println(l + " scores in file.");

			/* don't over-read either MAX_SCORES or l */
			while(i < l){
				scores[i] = (Score)in.readObject();
				//System.out.println("Reading: [" + i + "] " + scores[i]);
				i++;
			}
		}
		/* the file's corrupted; don't try to salvage anything */
		catch(ClassCastException e){
			return null;
		}
		catch(ClassNotFoundException e){
			return null;
		}
		/* no file; return blank */
		catch(FileNotFoundException e){
			return null;
		}
		/* perfectly normal, perfectly healthy... */
		catch(IOException e){}

		return scores;
	}

	/**
	* Write the given array of high scores to disk.
	* @param scores List of scores to write.
	*/
	public static void writeScores(Score[] scores){
		try{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(TetrisJ.SCORE_FILE));

			/* first the header, listing how many scores are here */
			out.writeInt(scores.length);
			//System.out.println(scores.length + " scores to write.");

			/* then each scores sequentially */
			for(int i = 0; i < scores.length && i < MAX_SCORES; i++){
				//System.out.println("Writing: " + scores[i]);
				out.writeObject(scores[i]);
			}
		}
		catch(IOException e){}
	}

	/**
	* Check given score, lines cleared, and time taken against the given
	* list of scores to see if they make the cut
	* @return true if the given stats could be added, false if not
	*/
	public static boolean isHighScore(long score, int lines, int time, Score[] scores){
		return isHighScore(new Score(null, score, lines, 0, time, 0), scores);
	}
	/** Same as the other <code>isHighScore</code> but with a full Score
	* object (though we only check the same three fields).
	* @return true if the given stats could be added, false if not
	*/
	public static boolean isHighScore(Score score, Score[] scores){
		if(scores == null || scores.length < MAX_SCORES) return true;
		java.util.Arrays.sort(scores);
		return (score.compareTo(scores[scores.length-1]) < 0);
	}

	/**
	* Add the given high score to the given list, if it's big enough.
	* @return the index of the item added, or -1 if it wasn't big enough to add
	*/
	public static Score[] addHighScore(Score score, Score[] scores){
		/* we don't do this the most efficient way, because it should only be
		 * 10 items, or something close to that. SMALL. */
		int len;
		/* guard against null scores[]. if it's null, it's effectively empty. */
		if(scores != null){
			java.util.Arrays.sort(scores);
			len = scores.length;
		}
		else{
			len = 0;
		}

		/* here we rely in the fact that isHigScore() sorts the scores it's given */
		if(isHighScore(score, scores)){
			if(len < MAX_SCORES){
				/* ugh, we have to reallocate scores[] and copy over the old data */
				Score[] newScores = new Score[len+1];

				/* mmm, memcpy() */
				if(scores != null)
					System.arraycopy(scores, 0, newScores, 0, len);

				newScores[len] = score;
				scores = newScores;
			}
			else{
				/* otherwise we just overwrite the last element */
				scores[scores.length-1] = score;
			}
			/* sort it again to properly place the new item */
			java.util.Arrays.sort(scores);
		}
		return scores;
	}
	
	/**
	* Clear the high scores list by deleting the high scores file.
	* @return true on success, false on failure
	*/
	public static boolean clearScores(){
			File f = new File(TetrisJ.SCORE_FILE);
			return f.delete();
	}

}
