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
 * $Id: TestGenerator.java,v 1.2 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.test.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Stack;


/**
 * Framework for generating compiler tests
 * 
 * @author Ivica Aracic
 */
public class TestGenerator {

    protected String workingDir = null;
    private Stack blockStack = new Stack();
    private FileOutputStream fos = null;
    
    public TestGenerator() {
        workingDir = ".";        
    }
    
    public TestGenerator(String workingDrectory) {
        this.workingDir = workingDrectory;
    }
    
    public void begin(String packageName, String testName, String purpose) throws GeneratorException {
        try {
            File f = new File(workingDir+"/"+packageName);
            try {
                f.delete();
            }
            catch (Exception e) {                
            }
            
            f.mkdir();
            
            fos = new FileOutputStream(workingDir+"/"+packageName+"/Test.java");
            
            writeLine("package generated."+packageName+";\n");            
            writeLine("/**");
            writeLine(" * Purpose: "+purpose);
            writeLine(" */");
            writeLine("class Test {}\n");
        }
        catch (Exception e) {
            throw new GeneratorException(e);
        }
    }

    public void beginCClass(String className, String extendsClause) throws GeneratorException {
        beginCClass(className, extendsClause, null, null);
    }
    
    public void beginCClass(String className, String extendsClause, String implClause, String wrapsClause) throws GeneratorException {
        StringBuffer line = new StringBuffer();
        line.append("public cclass "+className);
        if(extendsClause != null) line.append(" extends "+extendsClause);
        if(implClause != null) line.append(" implements "+implClause);
        if(wrapsClause != null) line.append(" wraps "+wrapsClause);
        line.append(" {");
        writeLine(line.toString());
        
        blockStack.push("}");
    }
    
    public void writeCClass(String className, String extendsClause, String implClause, String wrapsClause) throws GeneratorException {
        beginCClass(className, extendsClause, implClause, wrapsClause);
        end();
    }

    public void writeCClass(String className, String extendsClause) throws GeneratorException {
        beginCClass(className, extendsClause, null, null);
        end();
    }

    public void writeLine(String line) throws GeneratorException {
        try {
            int stackDepth = blockStack.size();
            
            for(int i=0; i<stackDepth; i++) {
                fos.write("    ".getBytes());
            }
            
            fos.write(line.getBytes());
            fos.write('\n');
        }
        catch (Exception e) {
            throw new GeneratorException(e);
        }
    }
    
    public void end() throws GeneratorException {
        if(blockStack.size() == 0) {
            try {            
	            fos.close();
	            fos = null;
            }
	        catch (Exception e) {
	            throw new GeneratorException(e);
	        }
        }
        else {
            String closingLine = (String)blockStack.pop();
            writeLine(closingLine);
        }
    }

}
