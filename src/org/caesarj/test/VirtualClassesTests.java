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
 * $Id: VirtualClassesTests.java,v 1.42 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.test;

public class VirtualClassesTests extends FjTestCase {

	public VirtualClassesTests(String name) {
		super(name);
	}

	/* Test factory methods late bound new */
	public void testCaesarTestCase_0() throws Throwable {
        compileAndRun("test0", "VCTestCase");
    }

	/* Test implements clause for cclass. */
    public void testCaesarTestCase_1() throws Throwable {
        compileAndRun("test1", "VCTestCase");
    }

    /* Test &-Operator and linearization (with graphs) */
    public void testCaesarTestCase_2() throws Throwable {
        compileAndRun("test2", "VCTestCase");
    }

    /* Test &-Operator and linearization (with graphs) */
    public void testCaesarTestCase_3() throws Throwable {
        compileAndRun("test3", "VCTestCase");
    }

    public void testCaesarTestCase_4() throws Throwable {
        compileAndRun("test4", "VCTestCase");
    }
    
    public void testCaesarTestCase_5() throws Throwable {
        compileAndRun("test5", "VCTestCase");
    }
 
    public void testCaesarTestCase_5b() throws Throwable {
        compileAndRun("test5b", "VCTestCase");
    }
 
    public void testCaesarTestCase_6() throws Throwable {
        compileAndRun("test6", "VCTestCase");
    }

    public void testCaesarTestCase_7() throws Throwable {
        compileAndRun("test7", "VCTestCase");
    }

	public void testCaesarTestCase_8() throws Throwable {
        compileAndRun("test8", "VCTestCase");
    }
	
	public void testCaesarTestCase_9() throws Throwable {
        compileAndRun("test9", "VCTestCase");
    }
	
	/*  Test factory methods of outer classes. */
	public void testCaesarTestCase_10() throws Throwable {
        compileAndRun("test10", "VCTestCase");
    }

	/* Inner classes of inner classes. */
	public void testCaesarTestCase_11() throws Throwable {
        compileAndRun("test11", "VCTestCase");
    }

	/* Test factory methods of inner classes. */
	public void testCaesarTestCase_12() throws Throwable {
        compileAndRun("test12", "VCTestCase");
    }

	/* Test inherited methods. */
	public void testCaesarTestCase_13() throws Throwable {
        compileAndRun("test13", "VCTestCase");
    }

	/* Test super calls. */
	public void testCaesarTestCase_14() throws Throwable {
        compileAndRun("test14", "VCTestCase");
    }

	/* Test super calls using extends for furtherbindings. */
    public void testCaesarTestCase_14b() throws Throwable {
        compileAndRun("test14b", "VCTestCase");
    }

    /* Test long inheritance sequence. */
	public void testCaesarTestCase_15() throws Throwable {
        compileAndRun("test15", "VCTestCase");
    }
	
	/* Test state inheritance. */
	public void testCaesarTestCase_16() throws Throwable {
        compileAndRun("test16", "VCTestCase");
    }
	
	/* Test extending deep classes relationships. */
	public void testCaesarTestCase_17() throws Throwable {
        compileAndRun("test17", "VCTestCase");
    }
	
	/* Test multiple inheritance of methods. */
	public void testCaesarTestCase_18() throws Throwable {
        compileAndRun("test18", "VCTestCase");
    }

	/* Test multiple inheritance. */
	public void testCaesarTestCase_18b() throws Throwable {
        compileAndRun("test18b", "VCTestCase");
    }

	/* Test multiple inheritance of state. */
	public void testCaesarTestCase_19() throws Throwable {
        compileAndRun("test19", "VCTestCase");
    }

	/* Test joins linearization of state. */
	public void testCaesarTestCase_20() throws Throwable {
        compileAndRun("test20", "VCTestCase");
    }
    
	/* Test joining extensions of state. */
    public void testCaesarTestCase_21() throws Throwable {
        compileAndRun("test21", "VCTestCase");
    }    
	
    /* Test polymorphism of joined classes. */
	public void testCaesarTestCase_22() throws Throwable {
        compileAndRun("test22", "VCTestCase");
    }    
    	
	/* Test join inner classes. */
	public void testCaesarTestCase_23() throws Throwable {
        compileAndRun("test23", "VCTestCase");
    }
	
	/* Test arrays on Caesar classes. */
	public void testCaesarTestCase_24() throws Throwable {
        compileAndRun("test24", "VCTestCase");
    }
	
	/* Test default constructors. */
	public void testCaesarTestCase_25() throws Throwable {
        compileAndRun("test25", "VCTestCase");
    }

	/* Test extends for furtherbindings. */
	public void testCaesarTestCase_26() throws Throwable {
        compileAndRun("test26", "VCTestCase");
    }
	
	/* Test multiple outer joins. */
	public void testCaesarTestCase_27() throws Throwable {
        compileAndRun("test27", "VCTestCase");
    }

	/* Test merging class with multiple parents. */
	public void testCaesarTestCase_28() throws Throwable {
        compileAndRun("test28", "VCTestCase");
    }
	
	/* Test introducing new inheritance. */
	public void testCaesarTestCase_29() throws Throwable {
        compileAndRun("test29", "VCTestCase");
    }

	/* Test merging class hierarchies. */
	public void testCaesarTestCase_30() throws Throwable {
        compileAndRun("test30", "VCTestCase");
    }
    
	/* Test outer class creation. */
    public void testCaesarTestCase_31() throws Throwable {
        compileAndRun("test31", "VCTestCase");
    }
    
    /* Test introducing new inheritance. */
    public void testCaesarTestCase_32() throws Throwable {
        compileAndRun("test32", "VCTestCase");
    }
    
    /* Test virtual class scoping */
    public void testCaesarTestCase_33() throws Throwable {
        compileAndRun("test33", "VCTestCase");
    }

    /* Test virtual class scoping with deeper nesting */
    public void testCaesarTestCase_33b() throws Throwable {
        compileAndRun("test33b", "VCTestCase");
    }

    /* Test mixin factory methods. */
	public void testCaesarTestCase_34() throws Throwable {
        compileAndRun("test34", "VCTestCase");
    }
	
	/* Test Object methods through Caesar interfaces. */
	public void testCaesarTestCase_35() throws Throwable {
        compileAndRun("test35", "VCTestCase");
    }
	
	/* Test inheritance cross package boundaries */
	public void testCaesarTestCase_36() throws Throwable {
        compileAndRun("test36", "VCTestCase");
    }
	
	/* Test instantiation cross package boundaries */
	public void testCaesarTestCase_36b() throws Throwable {
        compileAndRun("test36b", "VCTestCase");
    }
	
	/* Test automatic casts */
	public void testCaesarTestCase_37() throws Throwable {
        compileAndRun("test37", "VCTestCase");
    }
	
	/* Wrapper test */
	public void testCaesarTestCase_38() throws Throwable {
        compileAndRun("test38", "VCTestCase");
    }
	
	/* Wrapper test */
	public void testCaesarTestCase_39() throws Throwable {
        compileAndRun("test39", "VCTestCase");
    }
	
	/* Array Test (with board) */
	public void testCaesarTestCase_40() throws Throwable {
        compileAndRun("test40", "VCTestCase");
    }
	
	/* Accessing field within nested mixin copies */
	public void testCaesarTestCase_41() throws Throwable {
        compileAndRun("test41", "VCTestCase");
    }
	
	/* Accessing public cclass fields */
	public void testCaesarTestCase_50() throws Throwable {
        compileAndRun("test50", "VCTestCase");
    }
	
	/* Test subject oriented programming. */
	public void testCaesarTestCase_99() throws Throwable {
        compileAndRun("test99", "VCTestCase");
    }
}