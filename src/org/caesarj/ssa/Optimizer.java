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
 * $Id: Optimizer.java,v 1.1 2003-07-05 18:29:36 werner Exp $
 */

package org.caesarj.ssa;

import java.io.*;

import org.caesarj.classfile.ClassFileFormatException;
import org.caesarj.classfile.ClassInfo;
import org.caesarj.classfile.CodeInfo;
import org.caesarj.classfile.MethodInfo;

/**
 * This class is the entry point for the optimizer.
 */
public class Optimizer {

  /**
   * Reads, optimizes and writes a class file
   * @exception	UnpositionedError	an error occurred
   */
  public static void optimizeClass(ClassInfo info)  {
    MethodInfo[]	methods;
    
    methods = info.getMethods();
    for (int i = 0; i < methods.length; i++) {
      CodeInfo		code;

      code = methods[i].getCodeInfo();
      if (code != null) {
	MethodOptimizer methOptim = new MethodOptimizer(methods[i], code);
	code = methOptim.generateCode();
        methods[i].setCodeInfo(code);
      }
    }
  }
  /**
   * Reads, optimizes and writes a class file
   * @exception	UnpositionedError	an error occurred
   */
  public static MethodInfo optimize(MethodInfo info)  {
    CodeInfo		code;

    code = info.getCodeInfo();
    if (code != null) {
      MethodOptimizer methOptim = new MethodOptimizer(info, code);

      code = methOptim.generateCode();
      info.setCodeInfo(code);
    }
    return info;
  }
}
