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
