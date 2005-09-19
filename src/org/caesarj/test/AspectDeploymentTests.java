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
 * $Id: AspectDeploymentTests.java,v 1.25 2005-09-19 08:49:39 gasiunas Exp $
 */

package org.caesarj.test;

public class AspectDeploymentTests extends FjTestCase 
{
	public AspectDeploymentTests(String name) {
		super(name);
	}
	
	/* Test dynamic deployment with inheritance */
	public void testCaesarTestCase_0() throws Throwable {
		compileAndRun("test100", "ADTestCase");
	}

	/* Test longer inner aspect inheritance sequence */
	public void testCaesarTestCase_1() throws Throwable {
		compileAndRun("test101", "ADTestCase");
	}

	/* Test static aspect deployment */
	public void testCaesarTestCase_2() throws Throwable {
		compileAndRun("test102", "ADTestCase");
	}

	/* Test abstract crosscutting classes, implementing abstract pointcuts (not supported) */
	public void testCaesarTestCase_3() throws Throwable {
		compileAndRun("test103", "ADTestCase");
	}

	/* Test precedence declaration for crosscutting and deployed classes */
	public void testCaesarTestCase_4() throws Throwable {
		compileAndRun("test104", "ADTestCase");
	}
	
	/* Test after returning, after throwing, usage of join point reflection */
	public void testCaesarTestCase_5() throws Throwable {
		compileAndRun("test105", "ADTestCase");
	}

	/* Test around advices */
	public void testCaesarTestCase_6() throws Throwable {
		compileAndRun("test106", "ADTestCase");
	}

	/* Test thread safety of deployment */
	public void testCaesarTestCase_7() throws Throwable {
		compileAndRun("test107", "ADTestCase");
	}

	/* Test thread safety of deployment */
	public void testCaesarTestCase_9() throws Throwable {
		compileAndRun("test109", "ADTestCase");
	}
     
	/* Test statically deployed around advices */
	public void testCaesarTestCase_10() throws Throwable {
		compileAndRun("test110", "ADTestCase");
	}
	
	/* Test static aspect with concrete pointcut inherits
       from static abstract aspect with concrete advice. (not supported) */
	public void testCaesarTestCase_11() throws Throwable {
		compileAndRun("test111", "ADTestCase");
	}

	/* Test deployment of multiple instances with around advices */
	public void testCaesarTestCase_12() throws Throwable {
		compileAndRun("test112", "ADTestCase");
	}
	
	/* Test using Caesar type system for crosscutting */
	public void testCaesarTestCase_13() throws Throwable {
		compileAndRun("test113", "ADTestCase");
	}
	
	/* Test crosscutting outer joins */
	public void testCaesarTestCase_14() throws Throwable {
		compileAndRun("test114", "ADTestCase");
	}
	
	/* Test crosscuts in mixins */
	public void testCaesarTestCase_15() throws Throwable {
		compileAndRun("test115", "ADTestCase");
	}
	
	/* Test if mixins are weaved */
	public void testCaesarTestCase_16() throws Throwable {
		compileAndRun("test116", "ADTestCase");
	}

	/* Test conditional pointcuts (not supported yet) */
	public void testCaesarTestCase_17() throws Throwable {
		compileAndRun("test117", "ADTestCase");
	}
	
	/* Advices on inherited pointcuts */
	public void testCaesarTestCase_18() throws Throwable {
		compileAndRun("test118", "ADTestCase");
	}
	
	/* Test deploy block robustness */
	public void testCaesarTestCase_19() throws Throwable {
		compileAndRun("test119", "ADTestCase");
	}
	
	/* Test deployment of multiple objects on inherited registries */
	public void testCaesarTestCase_20() throws Throwable {
		compileAndRun("test120", "ADTestCase");
	}
	
	/* Test exception softening (not supported yet) */
	public void testCaesarTestCase_21() throws Throwable {
		compileAndRun("test121", "ADTestCase");
	}

	/* Test privileged access. (not supported yet) */
	public void testCaesarTestCase_22() throws Throwable {
		compileAndRun("test122", "ADTestCase");
	}
	
	/* Test multi-instance around calls */
	public void testCaesarTestCase_23() throws Throwable {
		compileAndRun("test123", "ADTestCase");
	}
	
	/* Test wrapping primitive types in around */
	public void testCaesarTestCase_24() throws Throwable {
		compileAndRun("test124", "ADTestCase");
	}
	
	/* Test passing join point reflection info */
	public void testCaesarTestCase_25() throws Throwable {
		compileAndRun("test125", "ADTestCase");
	}
	
	/* Test local deployment */
	public void testCaesarTestCase_26() throws Throwable {
		compileAndRun("test126", "ADTestCase");
	}
	
	/* Test cflow pointcuts */
	public void testCaesarTestCase_27() throws Throwable {
		compileAndRun("test127", "ADTestCase");
	}
	
	/* Test cross-thread cflow pointcuts */
	public void testCaesarTestCase_28() throws Throwable {
		compileAndRun("test128", "ADTestCase");
	}
	
	/* Test nested crosscutting classes */
	public void testCaesarTestCase_29() throws Throwable {
		compileAndRun("test129", "ADTestCase");
	}
	
	/* Test joinpoint throwing exception */
	public void testCaesarTestCase_30() throws Throwable {
		compileAndRun("test130", "ADTestCase");
	}
	
	/* Tests advice precedence inside a class */
	public void testCaesarTestCase_31() throws Throwable {
		compileAndRun("test131", "ADTestCase");
	}
	
	/* Tests deploying class without crosscuts */
	public void testCaesarTestCase_32() throws Throwable {
		compileAndRun("test132", "ADTestCase");
	}
	
	/*  Tests deploying fields in non-crosscutting class */
	public void testCaesarTestCase_33() throws Throwable {
		compileAndRun("test133", "ADTestCase");
	}
	
	/*  Tests declares in abstract classes */
	public void testCaesarTestCase_34() throws Throwable {
		compileAndRun("test134", "ADTestCase");
	}
	
	/*  Test aspect() method */
	public void testCaesarTestCase_35() throws Throwable {
		compileAndRun("test135", "ADTestCase");
	}
	
	/*  Test precedence declarations with + */
	public void testCaesarTestCase_36() throws Throwable {
		compileAndRun("test136", "ADTestCase");
	}
	
	/*  Test pointcut references */
	public void testCaesarTestCase_37() throws Throwable {
		compileAndRun("test137", "ADTestCase");
	}
	
	/*  Test linearization of crosscuts */
	public void testCaesarTestCase_38() throws Throwable {
		compileAndRun("test138", "ADTestCase");
	}
	
	/*  Test resolving copied advice */
	public void testCaesarTestCase_39() throws Throwable {
		compileAndRun("test139", "ADTestCase");
	}
	
	/*  Test resolving copied advice */
	public void testCaesarTestCase_40() throws Throwable {
		compileAndRun("test140", "ADTestCase");
	}
	
	/*  Test resolving copied around advice */
	public void testCaesarTestCase_41() throws Throwable {
		compileAndRun("test141", "ADTestCase");
	}
}