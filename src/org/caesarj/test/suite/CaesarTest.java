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
 * $Id: CaesarTest.java,v 1.1 2005-02-24 17:16:53 aracic Exp $
 */

package org.caesarj.test.suite;

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
    
    public abstract void test() throws Throwable; 
}