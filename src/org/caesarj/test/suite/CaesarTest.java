/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: CaesarTest.java,v 1.5 2005-12-16 16:29:43 klose Exp $
 */

package org.caesarj.test.suite;

import java.io.File;

import javax.security.auth.login.FailedLoginException;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public abstract class CaesarTest extends TestCase {
    private String id;
    private String description;
    private String codeBlock; 
    
    private CaesarTestSuite testSuite;    
    
    public CaesarTest(CaesarTestSuite testSuite, String id, String description, String codeBlock) {
        super("test");        
        this.testSuite = testSuite;
        this.id = id;
        this.description = description;
        this.codeBlock = codeBlock;
    }

    
    public CaesarTestSuite getTestSuite() {
        return testSuite;
    }
    
    public String getCodeBlock() {
        return codeBlock;
    }       
    
    public String getDescription() {
        return description;
    }
    
    public String getId() {
        return id;
    }
    
    public void failure(String message) {
        String 	prefix = testSuite.getOutputPath(),
				folder, name; 
        String[] comps = prefix.split("\\" + File.separator);
        int last = comps.length-1;
        
        String testFileName = "/caesar-compiler/tests/src/"+comps[last-1]+"/"+comps[last]+"."+id+".java";
        Assert.fail(message+" <caesartest file=\""+testFileName+"\" line=\"1\"/>");
    }
    
    public abstract void doTest() throws Throwable;
    
    public void test() throws Throwable {
        boolean success = true;
        Throwable error = null;
        try{
            doTest();
            testSuite.getTestLog().addResult(this, true, "");
            success = true;
        } catch( Throwable t ){
            testSuite.getTestLog().addResult(this, false, t.getMessage());
            success = false;
            error = t;
        }
        
        if( getTestSuite().isCompareMode()){
            CaesarTestrunLog lastRun = getTestSuite().getLastRun();

            if(lastRun.containsTest(id, getTestSuite().getName() )){
                if( lastRun.getTestResult(id, getTestSuite().getName()) != success ){
                    throw new AssertionFailedError("Test result differs from last run");
                }
            }
        } else {
            if (!success) {
                throw error;
            }
        }
        
    }
}
