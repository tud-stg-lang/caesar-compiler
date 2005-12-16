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
 * $Id: AllTests.java,v 1.7 2005-12-16 16:29:43 klose Exp $
 */

package org.caesarj.test.suite;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class AllTests {   
    
    public static Test suite() throws Exception {
        
		
		File workingDir = new File( TestProperties.instance().getWorkingDir() );
		File genSrcDir = new File( TestProperties.instance().getGenSrcDir() );
		File testDir =  new File( TestProperties.instance().getTestDir() );
		
		// clear all java and class files
		FileUtils.delAllFiles(workingDir, ".+\\.class");
		FileUtils.delAllFiles(workingDir, ".+\\.java");
		FileUtils.delAllFiles(workingDir, ".+\\.log");
		
		List testSuits = FileUtils.findAllFiles(
		    testDir, 
		    TestProperties.instance().getSuiteSearchPattern()
	    );			
		
        final String logFileName = TestProperties.instance().getLogFileName(); 
		File testLogFile = new File(logFileName);
		
        String testTitle = "Caesar Test Suite";

        CaesarTestrunLog lastRun=null, testLog=null;
        
        if (TestProperties.instance().isCompareMode()){
            try{
                lastRun = CaesarTestrunLog.loadFrom( testLogFile );
                testTitle += ", compared to last run";
                testLog = new CaesarTestrunLog( null );
            } catch( Exception e ){
                TestProperties.instance().setCompareMode(false);
            }
        } 

        if(!TestProperties.instance().isCompareMode()){
        
            if (TestProperties.instance().getKeepLogs()){
                createLogCopy(logFileName);
            }
            
            try{
                testLogFile.createNewFile();
            } catch(Exception e){
                System.err.println("Waring: Could not create new test log file: " + testLogFile +"\n"+ e);
            }

            testLog = new CaesarTestrunLog( testLogFile );
        }
        
        TestSuite suite = new TestSuite(testTitle);
        
		
		for (Iterator it = testSuits.iterator(); it.hasNext();) {
            File f = (File) it.next();
            
            String outputPath = genSrcDir.getAbsolutePath() +
            	f.getAbsolutePath().substring(testDir.getAbsolutePath().length());            
            
            CaesarTestSuite s = 
                CaesarTestSuite.parseXml(
                    testLog,
                    TestProperties.instance().isCompareMode(),
                    lastRun,
                    TestProperties.instance().getTestFilter(), 
                    f, 
                    outputPath);
            
            if(s.countTestCases() > 0) {
                suite.addTest( s );
            }
        }
		
		return suite;
	}

    private static void createLogCopy(final String logFileName) {
        File logFile = new File(logFileName);
        int lastDotIdx = logFileName.lastIndexOf('.');
        String backupName;
        Date date = Calendar.getInstance().getTime();
        String dateString = String.format("%1$4tY%1$2tm%1$2td%1$tH%1$tM%1$tS%1$tL", date );
        if(lastDotIdx > 0){
            backupName = logFileName.substring(0,lastDotIdx) + " - "+ 
                dateString+
                logFileName.substring(lastDotIdx);
        } else {
            backupName = logFileName + "- "+ dateString;
        }
        if (!logFile.renameTo( new File(backupName) )){
            System.err.println("Could not keep log");
        }
    }
    
    public static void main(String[] args) throws Exception {
        Test suite = suite();
    }    
}
