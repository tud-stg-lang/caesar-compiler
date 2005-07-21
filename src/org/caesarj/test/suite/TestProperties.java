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
 * $Id: TestProperties.java,v 1.4 2005-07-21 13:14:23 aracic Exp $
 */

package org.caesarj.test.suite;

import java.util.Properties;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class TestProperties {
	
	public final static String UNDEF_MESSAGE = "UNDEF_MESSAGE"; 

    private static TestProperties singleton = new TestProperties();

    private String suiteSearchPattern = ".+\\.suite\\.xml$";
    private String testFilter = ".+";
    
    private String workingDir = "tests";
    private String binDir = "tests/bin";
    private String genSrcDir = "tests/src";
    private String testDir = "tests/suits";
    
    private String logFileName = "tests/log/failure.log";
    
    private boolean ignoreUndefMsg = false;
    
    public static TestProperties instance() {
        return singleton;
    }
    
    private Properties props = new Properties();
    

    private TestProperties() {
        try {            
            props.load( 
                AllTests.class.getClassLoader().
                	getResourceAsStream("org/caesarj/test/suite/test.properties") 
        	);
            
            if(props.containsKey("suiteSearchPattern"))
                suiteSearchPattern = props.getProperty("suiteSearchPattern");

            if(props.containsKey("testFilter"))
                testFilter = props.getProperty("testFilter");

            if(props.containsKey("workingDir"))
                workingDir = props.getProperty("workingDir");
            
            if(props.containsKey("binDir"))
                binDir = props.getProperty("binDir");

            if(props.containsKey("testDir"))
                testDir = props.getProperty("testDir");

            if(props.containsKey("logFileName"))
                logFileName = props.getProperty("logFileName");
            
            if(props.containsKey("genSrcDir"))
                genSrcDir = props.getProperty("genSrcDir");
            
            if(props.containsKey("ignoreUndefMsg"))
            	ignoreUndefMsg = getBoolValue("ignoreUndefMsg");
        }
        catch (Exception e) {
            // do nothing, just continue with default values
        }
    }
    
    private boolean getBoolValue(String key) {
    	return props.getProperty(key).trim().toLowerCase().equals("true");
    }
    
    
//    public Properties getProps() {
//        return props;
//    }
    
    public String getSuiteSearchPattern() {
        return suiteSearchPattern;
    }
    
    public String getTestFilter() {
        return testFilter;
    }
        
    public String getBinDir() {
        return binDir;
    }
    
    public String getTestDir() {
        return testDir;
    }
    
    public String getLogFileName() {
        return logFileName;
    }
    
    public String getWorkingDir() {
        return workingDir;
    }
    
    public String getGenSrcDir() {
        return genSrcDir;
    }
    
    public boolean ignoreUndefMessages() {
    	return ignoreUndefMsg;
    }
}
