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
 * $Id: CReferenceType.java,v 1.17 2005-02-17 17:41:47 aracic Exp $
 */

package org.caesarj.compiler.types;

import java.util.Hashtable;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CBadClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.family.ContextExpression;
import org.caesarj.compiler.family.Path;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.SimpleStringBuffer;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents class type in the type structure
 */
public class CReferenceType extends CType {
    /**
     * Calculate the number of steps to the outer context needed to find
     * this type.
     * @param in The context to search in
     * @return The number of steps
     */
    public int getDefDepth(CContext in){
        int k = 0;	
        CClass myType = getCClass();
        
        // start with the block context if any
        if(in.getBlockContext() != null)
            in = in.getBlockContext();
        
        // find class context
        while ( !(in instanceof CClassContext) ){
            k++;
            in = in.getParentContext();
        }
        
        CClassContext classContext = (CClassContext) in;
        CClass 	ctx = classContext.getCClass(),
        		parent = myType.getOwner();
        
        if(ctx.isMixin())
            ctx = ctx.getMixinInterface();
        if(parent != null && parent.isMixin())
            parent = parent.getMixinInterface();
        
        while( ctx != parent && ctx != null){
//            if (ctx == null){
//                throw new InconsistencyException( ""+myType+" cannot be found in "+in);
//            }
            ctx = ctx.getOwner();
            k++;
        }
//        while (ctx != null){
//            k++;
//            ctx = ctx.getOwner();
//        }
        
        return k;
    }
    
    protected CContext declContext; /** the context in which this type was defined */
    
    public void setDeclContext(CContext declContext) {
        this.declContext = declContext;
    }
    
    public CContext  getDeclContext() {
        return declContext;
    }
    
    
    
    public Path getPath() throws UnpositionedError {        
        int k = getDefDepth(declContext);
        
        // if this type has been resolve in the context of a caesar accessor method
        // subtract 1 from k 
        // (we want ot start our path relative to the field decl rather than the context of the accessor method)
        if(declContext.getMethodContext() != null) {            
            if(declContext.getMethodContext().getMethodDeclaration().getMethod().isCaesarAccessorMethod())
                k--;
        }
        
        return new ContextExpression(null, k, null);
    }
    
	// ----------------------------------------------------------------------
	// CONSTRUCTORS
	// ----------------------------------------------------------------------

	/**
	 * Construct a class type
	 * @param	clazz		the class that will represent this type
	 */
	protected CReferenceType() {
		super(TID_CLASS);
		this.clazz = BAC_CLASS;
	}

	/**
	 * Construct a class type
	 * @param	clazz		the class that will represent this type
	 */
	public CReferenceType(CClass clazz) {
		super(TID_CLASS);
		this.clazz = clazz;

		if (!(this instanceof CArrayType)) {
			allCReferenceType.put(clazz.getQualifiedName(), this);
		}
	}

	public static CReferenceType lookup(String qualifiedName) {
		if (qualifiedName.indexOf('/') >= 0) {
			CReferenceType type =
				(CReferenceType) allCReferenceType.get(qualifiedName);

			if (type == null) {
				type = new CClassNameType(qualifiedName, false);
				allCReferenceType.put(qualifiedName, type);
			}

			return type;
		}
		else {
			return new CClassNameType(qualifiedName, false);
		}
	}

	public boolean isChecked() {
		return clazz != BAC_CLASS;
	}

	protected void setClass(CClass clazz) {
		this.clazz = clazz;
	}

	// ----------------------------------------------------------------------
	// ACCESSORS
	// ----------------------------------------------------------------------

	/**
	 * equals
	 */
	//   public boolean equals(CType other) {
	//     return (!other.isClassType() || other.isArrayType()) ?
	//       false :
	//       ((CReferenceType)other).getCClass() == getCClass();
	//   }

	/**
	 * Transforms this type to a string
	 */
	public String toString() {
		if (clazz.isNested()) {
			return clazz.getIdent();
		}
		else {
			return getQualifiedName().replace('/', '.');
		}
	}

	/**
	 * Appends the VM signature of this type to the specified buffer.
	 */
	public void appendSignature(SimpleStringBuffer buffer) {
		buffer.append('L');
		buffer.append(getQualifiedName());
		buffer.append(';');
	}
	/**
	 * @return the short name of this class
	 */
	public String getIdent() {
		return getCClass().getIdent();
	}

    /**
     *
     */
    public String getQualifiedName() {
        return getCClass().getQualifiedName();
    }

    /**
     *
     */
    public String getImplQualifiedName() {
        return getCClass().getImplQualifiedName();
    }

	/**
	 * Returns the stack size used by a value of this type.
	 */
	public int getSize() {
		return 1;
	}

	/**
	 * Check if a type is a reference
	 * @return	is it a type that accept null value
	 */
	public boolean isReference() {
		return true;
	}

	/**
	 * Check if a type is a class type
	 * @return	is it a subtype of ClassType ?
	 */
	public boolean isClassType() {
		return true;
	}

	/**
	 * Returns the class object associated with this type
	 *
	 * If this type was never checked (read from class files)
	 * check it!
	 *
	 * @return the class object associated with this type
	 */
	public CClass getCClass() {
		// !!! graf 000213
		// !!! should have been checked (see JFieldAccessExpression)
		verify(clazz != BAC_CLASS);
		// !!! graf 000213
		if (clazz == null) {
			if (this == CStdType.Object) {
				throw new InconsistencyException("java.lang.Object is not in the classpath !!!");
			}
			clazz = CStdType.Object.getCClass();
		}

		return clazz;
	}

	// ----------------------------------------------------------------------
	// INTERFACE CHECKING
	// ----------------------------------------------------------------------

	/**
	 * check that type is valid
	 * necessary to resolve String into java/lang/String
	 * @param	context		the context (may be be null)
	 * @exception UnpositionedError	this error will be positioned soon
	 */
	public CType checkType(CTypeContext context) throws UnpositionedError {
		return this;
	}

	// ----------------------------------------------------------------------
	// BODY CHECKING
	// ----------------------------------------------------------------------

	/**
	 * Can this type be converted to the specified type by assignment conversion (JLS 5.2) ?
	 * @param	dest		the destination type
	 * @return	true iff the conversion is valid
	 */
	public boolean isAssignableTo(CTypeContext context, CType dest) {
	    if (!(dest.isClassType() && !dest.isArrayType())) {
			return false;
		}

		return getCClass().descendsFrom(
			dest.getCClass());
	}	

	/** equals */
	public boolean equals(CType other) {
		if (other == this) {
			return true;
		}
		if (!other.isClassType()
			|| other.isArrayType()
			|| ((CReferenceType) other).getCClass() != getCClass()) {
			return false;
		}

		return true;
	}

	/**
	 * Can this type be converted to the specified type by casting conversion (JLS 5.5) ?
	 * @param	dest		the destination type
	 * @return	true iff the conversion is valid
	 */
	public boolean isCastableTo(CType dest) {
		// test for array first because array types are classes

		if (getCClass().isInterface()) {
			if (!dest.isClassType()) {
				return false;
			}
			else if (dest.getCClass().isInterface()) {
				// if T is an interface type and if T and S contain methods
				// with the same signature but different return types,
				// then a compile-time error occurs.
				//!!! graf 000512: FIXME: implement this test
				return true;
			}
			else if (!dest.getCClass().isFinal()) {
				return true;
			}
			else {
				return dest.getCClass().descendsFrom(getCClass());
			}
		}
		else {
			// this is a class type
			if (dest.isArrayType()) {
				return equals(CStdType.Object);
			}
			else if (!dest.isClassType()) {
				return false;
			}
			else if (dest.getCClass().isInterface()) {
				if (!getCClass().isFinal()) {
					return true;
				}
				else {
					return getCClass().descendsFrom(dest.getCClass());
				}
			}
			else {
				return getCClass().descendsFrom(dest.getCClass())
					|| dest.getCClass().descendsFrom(getCClass());
			}
		}
	}

	/**
	 *
	 */
	public boolean isCheckedException(CTypeContext context) {
		return !isAssignableTo(
			context,
			context.getTypeFactory().createReferenceType(
				TypeFactory.RFT_RUNTIMEEXCEPTION))
			&& !isAssignableTo(context,
				context.getTypeFactory().createReferenceType(
					TypeFactory.RFT_ERROR));
	}
	
	// ----------------------------------------------------------------------
	// INITIALIZERS METHODS
	// ----------------------------------------------------------------------

	public static void init(CompilerBase compiler) {
		allCReferenceType = new Hashtable(2000);
	}

	// ----------------------------------------------------------------------
	// DATA MEMBERS
	// ----------------------------------------------------------------------

	public static final CReferenceType[] EMPTY = new CReferenceType[0];	

	private static Hashtable allCReferenceType = new Hashtable(2000);
	private static final CClass BAC_CLASS = new CBadClass("<NOT YET DEFINED>");

	private CClass clazz;
}
