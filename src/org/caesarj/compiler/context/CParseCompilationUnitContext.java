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
 * $Id: CParseCompilationUnitContext.java,v 1.3 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.context;

import java.util.ArrayList;
import java.util.Stack;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.compiler.ast.phylum.JPackageName;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;

public class CParseCompilationUnitContext {
  public static CParseCompilationUnitContext getInstance() {
    return stack.size() == 0 ?
      new CParseCompilationUnitContext() :
      (CParseCompilationUnitContext)stack.pop();
  }

  public void release() {
    release(this);
  }

  public static void release(CParseCompilationUnitContext context) {
    context.clear();
    stack.push(context);
  }

  private CParseCompilationUnitContext() {
  }

  private void clear() {
    packageImports.clear();
    classImports.clear();
    typeDeclarations.clear();
    pack = null;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  public void setPackage(JPackageName pack) {
    packageName = pack == JPackageName.UNNAMED ? "" : pack.getName() + '/';
    this.pack = pack;
  }

  public void addPackageImport(JPackageImport pack) {
    packageImports.add(pack);
  }

  public void addClassImport(JClassImport clazz) {
    classImports.add(clazz);
  }

  public void addTypeDeclaration(ClassReader classReader, JTypeDeclaration decl) {
    typeDeclarations.add(decl);
    decl.generateInterface(classReader, null, packageName);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  public JPackageImport[] getPackageImports() {
    return (JPackageImport[])packageImports.toArray(new JPackageImport[packageImports.size()]);
  }

  public JClassImport[] getClassImports() {
    return (JClassImport[])classImports.toArray(new JClassImport[classImports.size()]);
  }

  public JTypeDeclaration[] getTypeDeclarations() {
    return (JTypeDeclaration[])typeDeclarations.toArray(new JTypeDeclaration[typeDeclarations.size()]);
  }

  public JPackageName getPackageName() {
    return pack;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JPackageName		pack;
  private String		packageName;
  private ArrayList		packageImports = new ArrayList();
  private ArrayList		classImports = new ArrayList();
  private ArrayList		typeDeclarations = new ArrayList();

  private static Stack		stack = new Stack();
}
