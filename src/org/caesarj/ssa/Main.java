/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: Main.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: Main.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

import java.io.*;

import org.caesarj.classfile.ClassFileFormatException;
import org.caesarj.classfile.ClassInfo;
import org.caesarj.classfile.CodeInfo;
import org.caesarj.classfile.MethodInfo;
import org.caesarj.compiler.UnpositionedError;

/**
 * This class is the entry point for the optimizer.
 */
public class Main {
    // --------------------------------------------------------------------
    // DATA MEMBERS
    // --------------------------------------------------------------------

    protected SSAOptions	options;

    // --------------------------------------------------------------------
    // CONSTRUCTORS
    // --------------------------------------------------------------------

    /**
     * Only main can construct Main
     */
    private Main(String[] args) {
	if (!parseArguments(args)) {
	    System.exit(1);
	}
	String[] infiles = args; //= options.nonOptions;
	if (infiles.length == 0) {
	    //options.usage();
	    System.err.println("bad usage");//BackendMessages.NO_INPUT_FILE);
	    System.exit(1);
	} else {
	    boolean errorsFound = false;
	    for (int i = 0; i < infiles.length; i++) {
		//if (options.verbose) {
		System.err.println("Processing " + infiles[i] + ".");
		//}
		try {
		    optimizeClass(infiles[i]);
		} catch (UnpositionedError e) {
		    System.err.println("Error: " + e.getMessage());
		    errorsFound = true;
		}
	    }
	    System.exit(errorsFound ? 1 : 0);
	}
    }
    // --------------------------------------------------------------------
    // ENTRY POINT
    // --------------------------------------------------------------------

    /**
     * Entry point to the assembler
     */
    public static void main(String[] args) {
	new Main(args);
    }
    /**
     * Reads, optimizes and writes a class file
     * @exception	UnpositionedError	an error occurred
     */
    public static void optimizeClass(ClassInfo info, SSAOptions options) throws UnpositionedError {
	MethodInfo[]	methods;
	int			length = 0;
	int			totalUnoptimized = 0;
	int			totalOptimized = 0;

	methods = info.getMethods();
	for (int i = 0; i < methods.length; i++) {
	    if (methods[i].getCodeInfo() != null) {
		if (options.verbose) {
		    length = methods[i].getCodeInfo().getInstructions().length;
		}

		optimizeMethod(methods[i], options);

		if (options.verbose) {
		    CodeInfo	code = methods[i].getCodeInfo();


		    System.err.println(methods[i].getName() + "\t[" + (code.getInstructions().length * 100.0 / length) + "]" +
				       length + " / " + code.getInstructions().length);
		    totalUnoptimized += length;
		    totalOptimized += code.getInstructions().length;
		}
	    }
	}

	if (options.verbose) {
	    System.err.println("TOTAL:\t[" + (totalOptimized * 100.0 / totalUnoptimized) + "]" +
			       totalOptimized + " / " + totalUnoptimized);


	}
    }
    // --------------------------------------------------------------------
    // ACTIONS
    // --------------------------------------------------------------------

    /**
     * Reads, optimizes and writes a class file
     * @exception	UnpositionedError	an error occurred
     */
    private void optimizeClass(String fileName) throws UnpositionedError {
	ClassInfo		info;
	MethodInfo[]	methods;

	info = readClassFile(fileName);

	optimizeClass(info, options);

	writeClassFile(info, options.destination == null ? fileName : options.destination + File.separatorChar + org.caesarj.util.Utils.splitQualifiedName(info.getName())[1] + ".class");
    }
    private static void optimizeMethod(MethodInfo method, SSAOptions options) {
	CodeInfo		code;

	code = method.getCodeInfo();
	if (code != null) {
	    long	length = code.getInstructions().length;

	    code = MethodOptimizer.optimize(method, code, options);

	    method.setCodeInfo(code);
	}
    }
    // --------------------------------------------------------------------
    // ACCESSORS
    // --------------------------------------------------------------------

    /*
     * Parse command line arguments.
     */
    private boolean parseArguments(String[] args) {
	options = new SSAOptions();
	if (!options.parseCommandLine(args)) {
	    return false;
	}
	return true;
    }
    private ClassInfo readClassFile(String fileName) throws UnpositionedError {
	try {
	    DataInputStream	in;
	    ClassInfo		info;

	    in = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName), 2048));
	    info = new ClassInfo(in, false);
	    in.close();

	    return info;
	} catch (ClassFileFormatException e) {
	    throw new UnpositionedError(SSAMessages.SEMANTIC_ERROR, new Object[] { fileName, e.getMessage() });
	} catch (IOException e) {
	    throw new UnpositionedError(SSAMessages.IO_EXCEPTION, new Object[] { fileName, e.getMessage() });
	}
    }
    private void writeClassFile(ClassInfo info, String fileName) throws UnpositionedError {
	try {
	    DataOutputStream	out;

	    out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));

	    info.write(out);
	    out.flush();
	    out.close();
	} catch (ClassFileFormatException e) {
	    throw new UnpositionedError(SSAMessages.SEMANTIC_ERROR, new Object[] { fileName, e.getMessage() });
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new UnpositionedError(SSAMessages.IO_EXCEPTION, new Object[] { fileName, e.getMessage() });
	}
    }
}
