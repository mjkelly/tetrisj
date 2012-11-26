/*-----------------------------------------------------------------
* Score.java -- Class to represent a single game score (for the high scores list).
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Fri Dec 31 01:46:48 PST 2004
-----------------------------------------------------------------*/
package TetrisJ;

import java.io.Serializable;
import java.util.Date;

public class Score implements Serializable, Comparable{
	public String name;
	public long score;
	public int lines, level, time;
	public long finished;

	/*
		name: display name
		score: score
		lines: how many lines cleared
		level: what level you were on
		time: how long it took, in seconds
		finished: when it ended, in seconds since the epoch (Jan 1 1970)
	 */
	public Score(String name, long score, int lines, int level, int time, long finished){
		this.name		= name; // Strings are immutable, so this is safe
		this.score		= score;
		this.lines		= lines;
		this.level		= level;
		this.time		= time;
		this.finished	= finished;
	}

	/* we do a little extra here to prevent identical scores from sorting arbitrarily */
	public int compareTo(Object o){
		Score other = (Score)o;
		if(this.score != other.score){
			/* why am I not doing this the easy way (this.score-other.score)?
			 * Because that returns a long, and I need an int. And just casting
			 * the result to an int wrecks the whole point of using longs in
			 * the first place. So it's worth a few extra lines. */
			if(this.score < other.score) return 1;
			else return -1;
			/* if it's not bigger, it's smaller, becase we already know it's
			 * not equal */
		}
		else{
			/* if scores are equal, whoever cleared more rows wins */
			/* (should this be FEWER lines instead? that's probably more
			 * impressive, but less intuitive.) */
			if(this.lines != other.lines){
				return (other.lines - this.lines);
			}
			else{
				/* if lines are equal, whoever did it in _LESS_ time wins */
				if(this.time > other.time) return 1;
				else if(this.time < other.time) return -1;
				else return 0;
				/* ...and if THAT's equal, somebody's probably feeding us fake
				 * values and it doesn't matter anyway ;) */
			}
		}
	}

	public boolean equals(Score other){
		if(other == null) return false;
		if(!(other instanceof Score)) return false;
		/* this is really what this should be, intuitively:
		return (this.score == other.score && this.finished == other.finished &&
				this.time == other.time   && this.level == other.level &&
				this.lines == other.lines && this.name.equals(other.name));

		But we don't do it that way, because I don't feel like making my
		compareTo() that complicated, and I'm never actually going to use
		equals() myself, anyway. So this way, compareTo() is shorter, and
		consistent with equals().
		*/
		return (this.score == other.score
				&& this.time == other.time
				&& this.lines == other.lines);
	}

	public String toString(){
		return name + ": " + score + " points and " + lines + " lines (level " + level + ") in " + TetrisJ.displayMilliSecs(time*1000) + " at " + (new Date(finished*1000));
	}
}

