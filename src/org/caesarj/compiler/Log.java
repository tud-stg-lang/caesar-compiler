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
