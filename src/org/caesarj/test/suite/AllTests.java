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
 * $Id: AllTests.java,v 1.2 2005-02-25 16:48:49 aracic Exp $
 */

package org.caesarj.test.suite;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class AllTests {

    public static final String WORKING_DIR = "tests";
    public static final String BIN_DIR = WORKING_DIR+File.separatorChar+"bin";
    public static final String TEST_DIR = WORKING_DIR+File.separatorChar+"suits";
    
    public static Test suite() throws Exception {
        
        String testSuffix = "suite.xml";
        
        try {
            Properties props = new Properties();
            props.load( 
                AllTests.class.getClassLoader().
                	getResourceAsStream("org/caesarj/test/suite/test.properties") 
        	);
            
            if(props.containsKey("test.suffix"))
                testSuffix = props.getProperty("test.suffix");
        }
        catch (Exception e) {
            // do nothing, just continue with default values
        }
        
		TestSuite suite = new TestSuite("Caesar Test Suite");
		
		File workingDir = new File(WORKING_DIR);
		
		// clear all java and class files
		FileUtils.delAllFiles(workingDir, ".class");
		FileUtils.delAllFiles(workingDir, ".java");
		FileUtils.delAllFiles(workingDir, ".log");
		
		List testSuits = FileUtils.findAllFiles(new File(TEST_DIR), testSuffix);			
		
		for (Iterator it = testSuits.iterator(); it.hasNext();) {
            File f = (File) it.next();
            CaesarTestSuite s = CaesarTestSuite.parseXml(f);
            
            suite.addTest( s );
        }
		
		return suite;
	}
    
    public static void main(String[] args) throws Exception {
        Test suite = suite();
    }
}
