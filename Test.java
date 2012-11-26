/*-----------------------------------------------------------------
* Test.java -- $desc$
* Copyright 2004 Michael Kelly (michael@michaelkelly.org, michaelkelly.org)
*
* This program is released under the terms of the GNU General Public
* License as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* Wed May  4 16:42:03 PDT 2005
-----------------------------------------------------------------*/

import java.util.Random;

public class Test {
	public static void main(String[] args) {
		Random r = new Random();

		for(int i = 0; i < 100; i++){
			System.out.println(r.nextInt());
		}
	}
}

