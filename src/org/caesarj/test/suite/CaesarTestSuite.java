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
 * $Id: CaesarTestSuite.java,v 1.3 2005-03-02 09:45:55 gasiunas Exp $
 */

package org.caesarj.test.suite;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestSuite;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTestSuite extends TestSuite {

    private String name;
    private String packagePrefix;
    private String outputPath;
    private File file;    
    
    private CaesarTestSuite(File file, String name, String outputPath) {
        super(name);
        this.file = file;
        this.outputPath = outputPath;
    }                
    
    public File getFile() {
        return file;
    }
    
    public String getOutputPath() {
    	return outputPath;
    }
        
    public String getPackagePrefix() {
        return packagePrefix;
    }
    
    public static CaesarTestSuite parseXml(String idFilter, File file, String outputPath) throws Exception {
        CaesarTestSuite res = new CaesarTestSuite(file, file.getName(), outputPath);
        
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(file);
        
        Element root = doc.getRootElement();
        
        if(!root.getName().equals("testsuite"))
            throw new Exception("<testsuite> expected, found" + root.getName());
        
        res.name = root.getAttributeValue("name");
        res.packagePrefix = root.getAttributeValue("package");
        
        if(res.name == null)
            throw new Exception("name attribute missing");
            
        if(res.packagePrefix == null)
            throw new Exception("package attribute missing");
        
        String commonCodeBase = "";
        List children = root.getChildren();
        for (Iterator it = children.iterator(); it.hasNext();) {
            Element item = (Element) it.next();
            
            if(item.getName().equals("common-code-base")) {
                commonCodeBase = item.getText();
            }
            else {
                String id;
                String description;
                String codeBlock;
                
                id = item.getAttributeValue("id");
                description = item.getAttributeValue("description");
                codeBlock = item.getChildText("code") + commonCodeBase;
                
                if( !id.matches(idFilter) ) 
                    continue;
                
	            if(item.getName().equals("compile-check-error")) {
	                String errorCode = item.getAttributeValue("error");
	                if(errorCode == null) throw new Exception("error attribute missing");
	                res.addTest(
	                    new CompileAndCheckErrorTest(res, id, description, codeBlock, errorCode)
	                );
	            }
	            else if(item.getName().equals("compile")) {
	                res.addTest(
	                    new CompileTest(res, id, description, codeBlock)
	                );
	            }
	            else if(item.getName().equals("compile-run")) {
	                String testMethodBlock = item.getChildText("test");                
	                res.addTest(
	                    new CompileAndRunTest(res, id, description, codeBlock, testMethodBlock)
	                );
	            }
	            else {
	                throw new Exception("<compile-check-error>, <compile>, or <compile-run> expected, found"+item.getName());
	            }
            }
        }
        
        return res;
    }
        
}
