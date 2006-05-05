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
 * $Id: CBinaryTypeContext.java,v 1.5 2006-05-05 14:00:42 gasiunas Exp $
 */

package org.caesarj.compiler.context;

import java.lang.ref.WeakReference;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMember;
import org.caesarj.compiler.types.SignatureParser;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.UnpositionedError;

/**
 * Context type checking. Also used while loading binary classes.
 */
public class CBinaryTypeContext implements CTypeContext{

  public CBinaryTypeContext(ClassReader classReader, TypeFactory typeFactory) {
    this(classReader, typeFactory, null, null, false);
  }

  public CBinaryTypeContext(ClassReader classReader, TypeFactory typeFactory, CTypeContext parent, CMember owner) {
    this(classReader, typeFactory, parent, owner, !owner.isStatic());
  }

  public CBinaryTypeContext(ClassReader classReader, 
                            TypeFactory typeFactory, 
                            CTypeContext parent, 
                            boolean parentLookup) {

    this(classReader, typeFactory, parent, null, parentLookup);
  }

  private CBinaryTypeContext(ClassReader classReader, 
                             TypeFactory typeFactory, 
                             CTypeContext parent, 
                             CMember owner, 
                             boolean parentLookup) {
    this.classReader = new WeakReference<ClassReader>(classReader);
    this.typeFactory = new WeakReference<TypeFactory>(typeFactory);
    this.parent = parent;
  }

  /**
   * @return the TypeFactory
   */
  public TypeFactory getTypeFactory() {
    return typeFactory.get();
  }

  /**
   * @return the Object used to read class files.
   */
  public ClassReader getClassReader() {
    return classReader.get();
  }

  /**
   * @return the user of the CClass, which wants to access it.
   */
  public CClassContext getClassContext(){
    if (parent == null) {
      return null;
    } else {
      return parent.getClassContext();
    }
  }

  /**
   * @param caller the user of the CClass, which wants to access it.
   * @param name the name of the class
   */
  public CClass lookupClass(CClass caller, String name) throws UnpositionedError {
    if (parent == null) {
      return null;
    } else {
      return parent.lookupClass(caller, name);
    }
  }

  /**
   * Reports a semantic error detected during analysis.
   *
   * @param	trouble		the error to report
   */
  public void reportTrouble(PositionedError trouble) {
    parent.reportTrouble(trouble);
  }


  public SignatureParser getSignatureParser() {
    return classReader.get().getSignatureParser();
  }
  
  public boolean allowsDependentTypes() {
	  return true;
  }

  private WeakReference<TypeFactory> typeFactory;
  private WeakReference<ClassReader> classReader; 
  private CTypeContext          parent;
}
