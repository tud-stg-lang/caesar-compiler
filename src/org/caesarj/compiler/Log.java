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
 * $Id: Log.java,v 1.4 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler;

import java.io.PrintWriter;

/**
 * Central Logger instance.
 * Note this class is not thread safe. Moreover, it is not possible to 
 * have different log targets per thread.
 * 
 * @author Ivica Aracic
 */
public class Log {

    private static PrintWriter errorOutput = new PrintWriter(System.err);
    private static PrintWriter warningOutput = new PrintWriter(System.err);
    private static PrintWriter verboseOutput = new PrintWriter(System.out);
    
    private static boolean verbose = false;
    
    public static void setVerbose(boolean mode) {
        Log.verbose = mode;
    }
    
    /*
    public void closeLog() {
        try {
            errorOutput.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            if(warningOutput != errorOutput)
                warningOutput.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            if(verboseOutput != warningOutput && verboseOutput != errorOutput)
                verboseOutput.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    
    public static void setErrorOutput(PrintWriter errorOutput) {
        Log.errorOutput = errorOutput;
    }
    
    public static void setVerboseOutput(PrintWriter verboseOutput) {
        Log.verboseOutput = verboseOutput;
    }
    
    public static void setWarningOutput(PrintWriter warningOutput) {
        Log.warningOutput = warningOutput;
    }
    
    public static void error(String msg) {
        errorOutput.println(msg);
        errorOutput.flush();
    }

    public static void warning(String msg) {
        warningOutput.println(msg);
        warningOutput.flush();
    }
    
    public static void verbose(String msg) {
        if(verbose) {
            verboseOutput.println(msg);
            verboseOutput.flush();
        }
    }

    public static void setOutput(PrintWriter diagnosticOutput) {
        setErrorOutput(diagnosticOutput);
        setWarningOutput(diagnosticOutput);
        setVerboseOutput(diagnosticOutput);
    }    
    
}
