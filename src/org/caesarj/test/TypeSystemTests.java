/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: TypeSystemTests.java,v 1.7 2005-01-27 15:21:43 aracic Exp $
 */

package org.caesarj.test;

public class TypeSystemTests extends FjTestCase {

	public TypeSystemTests(String name) {
		super(name);
	}

	/*
	 * Test Dependent Types defined within plain Java classes
	 */
	public void testCaesarTestCase_01() throws Throwable {
	    compileDontRun("typesystest01");
    }

	public void testCaesarTestCase_02() throws Throwable {
	    compileDontRun("typesystest02");
    }

	public void testCaesarTestCase_03() throws Throwable {
	    compileDontRun("typesystest03");
    }

	public void testCaesarTestCase_04() throws Throwable {
	    compileDontRun("typesystest04");
    }

	public void testCaesarTestCase_05() throws Throwable {	    
	    compileDontRun("typesystest05");
    }

	public void testCaesarTestCase_06() throws Throwable {
	    compileDontRun("typesystest06");
    }

	public void testCaesarTestCase_07() throws Throwable {
	    compileDontRun("typesystest07");
    }

	public void testCaesarTestCase_08() throws Throwable {
	    compileDontRun("typesystest08");
    }

	public void testCaesarTestCase_09() throws Throwable {
	    compileDontRun("typesystest09");
    }

	public void testCaesarTestCase_30() throws Throwable{
	    compileDontRun("typesystest30");
	}
	
	/*
	 * Test Dependent Types defined within cclass
	 */
	public void testCaesarTestCase_50() throws Throwable {
	    compileDontRun("typesystest50");
    }

	public void testCaesarTestCase_51() throws Throwable {
	    compileDontRun("typesystest51");
    }

	public void testCaesarTestCase_52() throws Throwable {
	    compileDontRun("typesystest52");
    }


	/*
	 * TEST ERRORS
	 */
	/*
	public void testCaesarTestCase_100() throws Throwable {
	    compileAndCheckErrors("typesystest100", new String[]{""});
    }
    */

}