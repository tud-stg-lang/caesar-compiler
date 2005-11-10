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
 * $Id: CaesarMessages.msg,v 1.36 2005-11-10 15:06:03 gasiunas Exp $
 */

package org.caesarj.compiler.constants

prefix Caesar

parent org.caesarj.util.Messages

error	WEAVING_FAILED
		"weaving failed"
		
error	PROCEED_OUTSIDE_AROUND_ADVICE
		"proceed() can only be used inside an around-advice"
		
error 	ADVICE_FLAGS
		"only strictfp modifier allowed for Advices"
		
error	WEAVER_ERROR
		"{0}"
		
error	ASPECTJ_ERROR
		"{0}"
		
warning	ASPECTJ_WARNING
		"{0}"

error	POINTCUTS_OR_ADVICES_IN_NON_CROSSCUTTING_CLASS
		"pointcuts and advices may be declared in crosscutting or statically deployed classes only"

error	DEPLOYED_CLASS_CONSTRUCTOR_NON_PRIVATE_OR_WITH_PARAMETER
		"the constructor of a statically deployed class must be private and must not have any parameters"
		
error	DEPLOYED_FIELD_NOT_FINAL_AND_STATIC
		"statically deployed fields must be declared final and static"
		
error	DEPLOYED_CLASS_NOT_CROSSCUTTING
		"only instances of crosscutting classes can be deployed"
		
error	DESCENDANT_OF_CROSSCUTTING_CLASS_NOT_DECLARED_CROSSCUTTING
		"descendants of crosscutting classes must be declared crosscutting"


// VIRTUAL CLASSES

error	ONLY_PUBLIC_CCLASS
		"Current Caesar version allows only a public cclass."

error	ONLY_DEF_CTOR_ALLOWED 
		"A virtual class may have only a default constructor."
				
error	NAME_CLASH
		"Name clash for member \"{0}\" detected."

error	CLASS_GENERATOR
		"Class Generator failed on \"{0}\": {1}."

error	CCLASS_ARRAYS_NOT_ALLOWED
		"Currently arrays of Caesar classes are not supported."

error	CCLASS_EXTENDS_ONLY_IDENTIFIER_ALLOWED
		"Only single identifier allowed in the extends clause of an inner cclass."

error	CCLASS_SUPER_NOT_FOUND
		"Can not find the super cclass {0} in the direct enclosing class and its parents."

error	TOPLEVEL_CCLASS_EXTENDS_TOPLEVEL_CLASS
		"A top-level cclass may only extend from another top-level cclass."

error	CCLASS_EXTENDS_FROM_CCLASS
		"A cclass may only extend from another cclass."

error	CCLASS_CANNOT_ABSTRACT
		"Currently Caesar compiler does not support abstract cclass-es."

error	TOPLEVEL_CCLASS_WRAPS
		"Wraps clause not allowed for a top-level cclass."

error	CCLASS_CTOR_PUBLIC
		"cclass constructor has to be public."

error	OVERRIDE_WRAPPER
		"Can not override an existing wrappee."

error	CCLASS_PACKAGE_VISIBILITY
		"No package visibility for cclass members."

error	ERROR_ADJUSTING_MIXIN_SUPERTYPE
		"Error adjusting mixin's super type."
		
error	READ_ONLY_ACCESS_TO_CCLASS_FIELDS
		"Read-Only access for cclass fields."
		
// dependent types
error	ASSIGNMENT_MIXEDTYPES
		"Can not mix dependent and plain types."
		
error	PLAINTYPE_WITH_PATH
		"Path not allowed for plain Java types."
		
error	INNER_PLAIN_JAVA_OBJECTS_IN_PATH
		"References to inner Java classes not allowed in the path."

error	ILLEGAL_PATH_ELEMENT
		"{0} is an illegal path element."

error	ILLEGAL_PATH
		"Illegal family path. The reason could be that a dependent type path is not final."

error	PREFIX_FAMILY_PATH_NOT_AVAILABLE
		"Can not evaluate the prefix expression of '{0}' to a family path."

error	DECLARATION_DEPENDENCY_ORDER
		"The declaration of the first variable access in the path has to be before the declaration of the dependent type."
		
error   ABSTRACT_CANNOT_OVERRIDE_CONCRETE
		"A concrete virtual class cannot be overriden by an abstract one"
		
error   CANNOT_DEPLOY_ABSTRACT
		"Cannnot statically deploy an abstract class"
		
error   CANNOT_DEPLOY_VIRTUAL
		"Cannnot statically deploy a virtual class"
		
error   CCLASS_CANNOT_NEST_CLASS
		"Cannot include Java class to a cclass"
		
error   CANNOT_FIND_ENCLOSING_CCLASS
		"Cannot find the enclosing cclass"
		
error	MIXER_ERROR
		"Failed to generate mixin list for class \"{0}\": {1}."
		
error	POINTCUT_SYNTAX_ERROR
		"Syntax error in pointcut: {0}"

error	DECLARE_SYNTAX_ERROR
		"Syntax error in declaration: {0}"
		
error	CCLASS_UNKNOWN
		"Cannot find cclass \"{0}\" in this context"

error	CANNOT_INHERIT_FROM_CCLASS
		"Cannot inherit from cclass \"{0}\" in this context"		
		
error	CANNOT_INHERIT_FROM_OWN_OUTER
		"Class \"{0}\" inherits from its own enclosing class."
		
error   ANONYMOUS_CLASS_IN_CCLASS
		"Anonymous classes cannot be used in a context of a cclass."	