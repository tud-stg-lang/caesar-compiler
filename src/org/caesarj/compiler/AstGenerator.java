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
 * $Id: AstGenerator.java,v 1.7 2005-03-29 09:45:08 gasiunas Exp $
 */

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
    
    public JMethodDeclaration endMethod(String name) {
        JMethodDeclaration res = createMethodDeclaration(name, methodBuffer.toString());
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
    
    public JStatement[] endBlock(String name) {
        JStatement[] res = createBlock(name, bodyBuffer.toString());
        bodyBuffer = new StringBuffer();
        return res;
    }
    
    public JMethodDeclaration createMethodDeclaration(String name, String method) {
        JMethodDeclaration res = null;
        try {            
	        File f = File.createTempFile("caesar", name);
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
	            buffer.close();
	            f.delete();
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
    
    public JStatement[] createBlock(String name, String code) {
        JStatement[] stmts = null;
        try {            
	        File f = File.createTempFile("caesar", name);
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
	            buffer.close();
	            f.delete();
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
