package org.caesarj.compiler;

import java.io.File;
import java.io.FileOutputStream;

import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.tools.antlr.extra.InputBuffer;
import org.caesarj.tools.antlr.runtime.ParserException;

/**
 * ... 
 * 
 * @author Ivica Aracic 
 */
public class AstGenerator {       
    
    private KjcEnvironment environment;
    private KjcOptions options;
    private CompilerBase base;
    
    private StringBuffer methodBuffer = new StringBuffer();
    private StringBuffer bodyBuffer = new StringBuffer();
    
    public AstGenerator(CompilerBase base, KjcEnvironment environment, KjcOptions options) {
        this.environment = environment;
        this.options = options;
        this.base = base;
    }
    
    public void writeMethod(String[] lines) {
    	for (int i1 = 0; i1 < lines.length; i1++) {
    		methodBuffer.append(lines[i1]);
    		methodBuffer.append('\n');
    	}
    }
    
    public void writeMethod(String line) {
        methodBuffer.append(line);
    }   
    
    public JMethodDeclaration endMethod() {
        JMethodDeclaration res = createMethodDeclaration(methodBuffer.toString());
        methodBuffer = new StringBuffer();
        return res;
    }
    
    public void writeBlock(String[] lines) {
    	for (int i1 = 0; i1 < lines.length; i1++) {
    		bodyBuffer.append(lines[i1]);
    	}
    }
    
    public void writeBlock(String line) {
        bodyBuffer.append(line);
    }   
    
    public JStatement[] endBlock() {
        JStatement[] res = createBlock(bodyBuffer.toString());
        bodyBuffer = new StringBuffer();
        return res;
    }
    
    public JMethodDeclaration createMethodDeclaration(String method) {
        JMethodDeclaration res = null;
        try {            
	        File f = File.createTempFile("caesar","tmpmethod");
	        FileOutputStream fout = new FileOutputStream(f);
	        fout.write(method.getBytes());
	        fout.close();
	        
	        InputBuffer buffer = null;
	        try {
	            buffer = new InputBuffer(f, options.encoding);
	        }
	        catch (Exception e) {
	            e.printStackTrace();	            
	        }
	        
	        CaesarParser parser;

	        parser = new CaesarParser(base, buffer, environment);

	        try {
	            res = parser.jsMethodDefinition();
	        }
	        catch (ParserException pe) {
	            pe.printStackTrace();
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return res;
    }
    
    public JStatement[] createBlock(String code) {
        JStatement[] stmts = null;
        try {            
	        File f = File.createTempFile("caesar","tmpblock");
	        FileOutputStream fout = new FileOutputStream(f);
	        fout.write(code.getBytes());
	        fout.close();
	        
	        InputBuffer buffer = null;
	        try {
	            buffer = new InputBuffer(f, options.encoding);
	        }
	        catch (Exception e) {
	            e.printStackTrace();	            
	        }
	        
	        CaesarParser parser;

	        parser = new CaesarParser(base, buffer, environment);

	        try {
	            stmts = parser.jCompoundStatement();
	        }
	        catch (ParserException pe) {
	            pe.printStackTrace();
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return stmts;
    }
}
