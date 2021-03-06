/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: CBinaryType.java,v 1.5 2005-11-02 15:42:58 gasiunas Exp $
 */

package org.caesarj.compiler.types;

import java.lang.ref.WeakReference;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents class type load from a binary class file The class of
 * this type is only loaded if necessary. This type need no check
 */
public class CBinaryType extends CReferenceType {

    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------

    /**
     * Constructs a class or interface type from binary class
     */
    public CBinaryType(
        String qualifiedName,
        ClassReader classReader,
        TypeFactory typeFactory) {
        super();
        this.qualifiedName = qualifiedName;
        this.classReader = new WeakReference<ClassReader>(classReader);
        this.typeFactory = new WeakReference<TypeFactory>(typeFactory);
    }

    /**
     * Returns the class object associated with this type
     * 
     * If this type was never checked (read from class files) check it!
     * 
     * @return the class object associated with this type
     */
    public CClass getCClass() {
        if (!isChecked()) {
            setClass(classReader.get().loadClass(typeFactory.get(), qualifiedName));
            qualifiedName = null;
            classReader = null;
            typeFactory = null;
        }

        return super.getCClass();
    }

    public CType checkType(CTypeContext context) throws UnpositionedError {
        // IVICA make call to getCClass in order to load the class via the class reader.
        // looks like somebody very smart has developed this lazy loading mechanism,
        // but forgot to implement the checkType method. This has caused crashes with arrays 
        // as method return type.
        // buuuuuh! ;p
        getCClass();
        return super.checkType(context);
    }

    /**
     *  
     */
    public String getQualifiedName() {
        return qualifiedName == null ? super.getQualifiedName() : qualifiedName;
    }

    private String qualifiedName;

    private WeakReference<ClassReader> classReader;

    private WeakReference<TypeFactory> typeFactory;
}