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
 * $Id: CaesarMessages.msg,v 1.9 2003-08-27 22:38:42 werner Exp $
 */

package org.caesarj.compiler

prefix Caesar

parent org.caesarj.compiler.CompilerMessages

// FJ errors
error		FJ_IMPL_WITHOUT_FJ_PARSER
		"FamilyJ implementations e.g. FjVisitors are only compatible to a FjParser"
		"WRONG PARSER"

// FJLS messages
error		CLEAN_CLASS_NO_INNER
		"clean classes may not be inners"
		"FJLS 1.1"

error		MODIFIERS_INNER_CLASSES_ONLY
		"\"{0}\" can only be applied to inner classes"
		"FJLS 1.2"

error		METHOD_VISIBILITY_IN_CLEAN_CLASS
		"clean, virtual or override class method \"{0}\" must have public or private visibility"
		"FJLS 1.3"

error		CLEAN_JUST_PRIVATE_FIELDS
		"clean, virtual or override class field \"{0}\" must have private visibility"
		"FJLS 1.4"

error		CLEAN_NO_NON_CLEAN_MEMBER
		"clean, virtual or override class may not declare non-clean inner \"{0}\""
		"FJLS 1.5"

error		CLEAN_INHERITS_NON_CLEAN
		"clean, virtual or override class may not inherit non clean class \"{0}\""
		"FJLS 1.6"

error		NON_CLEAN_INHERITS_CLEAN
		"non clean or virtual class may not inherit clean class \"{0}\""
		"FJLS 1.7"

error		CLEAN_ABSTRACT_CLASS
		"clean, virtual or override class may not be declared abstract"
		"FJLS 1.8"

error		CLEAN_CLASS_THISCALL
		"clean, virtual or override class constructors may not issue this constructor calls"
		"FJLS 1.9"

warning		MISSING_CONSTRUCTOR
		"constructor \"{0}\" is not defined by class \"{1}\" and has been inherited"
		"FJLS 1.10"

error		NO_MATCHING_OVERRIDDEN_CLASS
		"overridden class not found"
		"FJLS 1.11"

error		MUST_BE_VIRTUAL
		"class \"{0}\" must be declared virtual"
		"FJLS 1.12"

error		FINAL_REFERENCE_NEEDED
		"\"{0}\" has to be a final reference"
		"FJLS 2.1"

error		PRIVATE_REFERENCE_NEEDED
		"reference to \"{0}\" has to be private"
		"FJLS 2.2"

error		WRONG_FAMILY
		"expected family \"{0}\" found \"{1}\""
		"FJLS 2.3"

error		NO_THIS_FAMILY
		"receiver of method \"{0}\" cannot act as family"
		"FJLS 2.4"

error		NO_OUTERTHIS_FAMILY
		"no family declared for receiver of method \"{0}\""
		"FJLS 2.5"




info 	WEAVING_STARTED
		"[ weaving started ]"
		
info 	WEAVING_ENDED
		"[ weaving ended ]"		

info	WROTE_CLASS_FILE
		"[ wrote class file: {0} ]"

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

		
//Cci errors

error   PROVIDED_METHOD_FLAGS
        "Provided method flags in \"{0}\" must be some of: provided, public, abstract"

error   EXPECTED_METHOD_FLAGS
        "Expected method flags in \"{0}\" must be some of: expected, public, abstract"

error   PROVIDED_AND_EXPECTED_METHOD
		"Method \"{0}\" cannot be provided and expected."

error   COLLABORATION_METHOD_OUT_CI
		"The method \"{0}\" must be defined in a collaboration interaface."

error   CI_METHOD_FLAGS
		"Method \"{0}\" must be provided or expected."

error   NON_CI
		"Interface \"{0}\" must be a collaboration interface."

error   OWNER_IS_NOT_CI
		"Collaboration interface \"{0}\" must be declared inside a collaboration interface."

error   CI_MULTIPLE_SUPER_TYPE
		"Collaboration interface \"{0}\" cannot extend more than one collaboration interface."

error   EXPECTED_METHOD_IN_PROVIDING
		"Expected method \"{0}\" cannot be implemented in a providing class."
		
error   PROVIDED_METHOD_IN_BINDING
		"Provided method \"{0}\" cannot be implemented in a binding class."
		
error   MUST_IMPLEMENT_EXPECTED_METHOD
		"Expected method \"{0}\" must be implemented by the binding class \"{1}\"."
		
error   MUST_IMPLEMENT_PROVIDED_METHOD
		"Provided method \"{0}\" must be implemented by the providing class \"{1}\"."

error   CI_METHOD_FLAGS_CHANGED_IN_OVERRIDING
		"The method \"{0}\" cannot change the modifier when overriding a collaboration method."

error   NESTED_TYPE_NOT_PROVIDED
		"The nested type \"{0}\" from the collaboration interface \"{1}\" must be implemented."

error   NESTED_TYPE_NOT_BOUND
		"The nested type \"{0}\" from the collaboration interface \"{1}\" must be bound."

error   PROVIDING_DEFINES_OTHER_NESTED
		"The class \"{0}\" is not defined in the collaboration interface."
		
error   CLASS_IMPLEMENTS_N_CIS
		"The class \"{0}\" cannot implement more than one CI."
		
error   REFERENCE_IS_NOT_BINDING
		"\"{0}\" is not a binding class."

error   REFERENCE_IS_NOT_PROVIDING
		"\"{0}\" is not a providing class."

error   BINDING_OTHER_CI
		"\"{0}\" does not bind the collaboration interface \"{1}\"."

error   PROVIDING_OTHER_CI
		"\"{0}\" does not provide the collaboration interface \"{1}\"."

error   BINDING_CI_WITH_SAME_NAME
		"\"{0}\" cannot be declared with the same name of collaboration interface \"{1}\"."
		
error   PROVIDING_DEFINES_CONSTRUCTOR
		"Providing class \"{0}\" cannot define constructors with parameters."

error   BINDING_PROVIDING_DIRECT_CREATION
		"Providing or binding class \"{0}\" cannot be directly created."

error   WRAPPER_DEFINES_CONSTRUCTOR
		"Explicity declared wrapper class \"{0}\" cannot define constructors with parameters."

error   NON_BINDING_WRAPPER
		"Class \"{0}\" cannot define \"wraps\" clause because it does not bind a collaboration interface."

error   WRAPPER_DESTRUCTOR_NOT_FOUND
		"Cannot find wrapper \"{0}\" with matching parameters."

error   SUPER_CLASS_WRAPS
		"Class \"{0}\" cannot define \"wraps\" clause because its super class already wraps a class."

error   CLASS_DOES_NOT_WRAP
		"Class \"{0}\" does not wrap any class, so \"wrappee\" is not available."

error   VIRTUAL_ACCESSING_OUTER_PRIVATE
		"Virtual or override class \"{0}\" cannot access private methods or fields of the enclosing class."

error   VIRTUAL_CALLING_OUTER_OUTER
		"Virtual or override class \"{0}\" cannot call methods or access fields defined in the enclosing class of its outer class."

error   WEAVELET_NESTED
		"Weavelet class \"{0}\" cannot be an inner class."	

error   METHOD_NOT_FOUND_INSIDE_VIRTUAL
		"Cannot find method \"{0}\". Maybe it is a private method in the clean eclosing class that is not accessible inside virtual classes."