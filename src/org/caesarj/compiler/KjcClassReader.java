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
 * $Id: KjcClassReader.java,v 1.5 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler;

import java.util.HashSet;
import java.util.Hashtable;

import org.caesarj.classfile.ClassInfo;
import org.caesarj.classfile.ClassPath;
import org.caesarj.compiler.context.CBinaryTypeContext;
import org.caesarj.compiler.export.CBinaryClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.types.SignatureParser;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.UnpositionedError;

/**
 * This class implements the conceptual directory structure for .class files
 */
public class KjcClassReader extends org.caesarj.util.Utils implements ClassReader{

  public KjcClassReader(String classp, String extdirs, SignatureParser signatureParser) {
    classpath = new ClassPath(classp, extdirs);
    this.signatureParser = signatureParser;
  }
  // ----------------------------------------------------------------------
  // LOAD CLASS
  // ----------------------------------------------------------------------

  /**
   * Loads class definition from .class file
   */
  public CClass loadClass(TypeFactory typeFactory, String name) {
    CClass		cl = (CClass)allLoadedClasses.get(name);

    if (cl != null) {
      // look in cache
      return cl != CClass.CLS_UNDEFINED ? cl : null;
    } else {
      ClassInfo		file = classpath.loadClass(name, true);

      cl = file == null ? CClass.CLS_UNDEFINED : new CBinaryClass(signatureParser, this, typeFactory, file);
      allLoadedClasses.put(name, cl);
      if (cl instanceof CBinaryClass) {
        try {
          ((CBinaryClass)cl).checkTypes(new CBinaryTypeContext(this, typeFactory, null, cl));
        } catch (UnpositionedError e) {
          e.addPosition(org.caesarj.util.TokenReference.NO_REF);
          e.printStackTrace();
          throw new InconsistencyException("Error while reading class");
        }
      }

      return cl;
    }
  }

  /**
   * @return  false if name exists for source class as source class
   *          in an other file
   * @param CClass a class to add (must be a CSourceClass)
   */
  public boolean addSourceClass(CSourceClass cl) {
    CClass	last = (CClass)allLoadedClasses.put(cl.getQualifiedName(), cl);
    
    allLoadedPackages.add(cl.getPackage());
    
    // IVICA: what does the other checks below mean!?
    return last == null;
    /*
    return (last == null)
      || (cl.getOwner() != null)
      || !(last instanceof CSourceClass)
      || last.getSourceFile() == cl.getSourceFile();
   */
  }

  /**
   * @return a class file that contain the class named name
   * @param name the name of the class file
   */
  public boolean hasClassFile(String name) {
    CClass		cl = (CClass)allLoadedClasses.get(name);
    return (cl != null && cl != CClass.CLS_UNDEFINED) || (classpath.loadClass(name, true) != null);
  }

  public SignatureParser getSignatureParser() {
    return signatureParser;
  }

  /**
   * Returns ture iff the specified package exists in the classpath
   *
   * @param	name		the name of the package
   */
  public boolean packageExists(String name) {
    if (allLoadedPackages.contains(name)) {
      return true;
    } else {
      return classpath.packageExists(name);
    }
  }
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Hashtable	allLoadedClasses = new Hashtable(2000);
  private HashSet	allLoadedPackages = new HashSet(2000);
 //  private TypeFactory   typeFactory;
  private ClassPath     classpath;
  private final SignatureParser signatureParser;
}
