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
 * $Id: CaesarTestrunLog.java,v 1.1 2005-12-16 16:29:43 klose Exp $
 */
package org.caesarj.test.suite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Test;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author Karl Klose
 */
public class CaesarTestrunLog {
    
    private final File outputFile;
    private Document log;    
    private Element root;
    
    public static class TestResult{
        public final String id;
        public final String suite;
        public final boolean result;
        public final String message;
        
        public TestResult(final String id, final String suite,
                final boolean result, final String message) {
            super();
            this.id = id;
            this.suite = suite;
            this.result = result;
            this.message = message;
        }
    }
    
    public boolean containsTest(final String id, final String suite){
        Collection<Element> tests = root.getChildren("result");
        for(Element test : tests){
            String testId = test.getAttributeValue("id"),
                    testSuite = test.getAttributeValue("suite"); 
            if (testId.equals(id) && 
                testSuite.equals(suite)){
                return true;
            }
        }
        return false;
    }

    public boolean getTestResult(final String id, final String suite){
        Collection<Element> tests = root.getChildren("result");
        for(Element test : tests){
            if (test.getAttributeValue("id").equals(id) && 
                test.getAttributeValue("suite").equals(suite)){
                String successValue = test.getAttributeValue("success"); 
                return successValue.equals("true");
            }
        }
        return false;
    }
    
    
    public CaesarTestrunLog(File outputFile){
        this.outputFile = outputFile;
        log = new Document();
        root = new Element("testrun");
        log.setRootElement( root );
    }
     
    public void addTest(Test test){
        if (test instanceof CaesarTest){
            CaesarTest ctest = (CaesarTest) test;
            Element testsEl = root.getChild("tests");
            if(testsEl==null){
                testsEl = new Element("tests");
                root.getChildren().add(testsEl);
            }
            Element testEl = new Element("test");
            testEl.setAttribute("id",ctest.getId());
            testEl.setAttribute("suite", ctest.getTestSuite().getName() );
            testsEl.getChildren().add(testEl);
            write();
        }
    }

    public void write(){
        try{
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(log, new FileOutputStream(outputFile));
        } catch( Exception e ){
            // silent recover
        } 
    }
    
    public void addResult(CaesarTest test, boolean success, String message){
        Element result = new Element("result");
        result.setAttribute("id", test.getId());
        result.setAttribute("suite", test.getTestSuite().getName());
        result.setAttribute("success", (success? "true": "false"));
        if (!message.equals("")){
            result.setAttribute("message", message);
        }
        root.getChildren().add(result);
        write();
    }
    
    public void close(){
        try{
            XMLOutputter outputter = new XMLOutputter();
            outputter.output(log, new FileOutputStream(outputFile));
        } catch( Exception e ){
            // silent recover
        } 
    }
    
    // TODO: DEBUG ONLY
    protected void finalize() throws Throwable {
    }

    public static CaesarTestrunLog loadFrom(File testLogFile) throws JDOMException, IOException {
        CaesarTestrunLog l = new CaesarTestrunLog(null);
        SAXBuilder builder = new SAXBuilder();
        l.log = builder.build(testLogFile);
        l.root = l.log.getRootElement();
        return l;
    }
    
}
