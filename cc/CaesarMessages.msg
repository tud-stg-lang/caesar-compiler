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
 * $Id: CaesarMessages.msg,v 1.12 2004-03-22 12:59:12 aracic Exp $
 */

package org.caesarj.compiler.constants

prefix Caesar

parent org.caesarj.util.Messages



info	COMPILATION_STARTED 
		"[ start compilation in verbose mode ]"
		
info	FILE_PARSED 
		"[ parsed {0} in {1} ms ]"

info	INTERFACES_CHECKED
		"[ checked interfaces in {0} ms ]"

info	BODY_CHECKED
		"[ checked body of {0} in {1} ms ]"

info	CONDITION_CHECKED
		"[ checked condition of {0} in {1} ms ]"

info	CLASSFILE_GENERATED
		"[ optimized and generated {0} in {1} ms ]"

info	JAVA_CODE_GENERATED
		"[ generated {0} ]"

info	CLASS_LOADED 
		"[ loaded {0} ]"
	
info	COMPILATION_ENDED 
		"[ compilation ended ]"


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


error	VIRTUAL_ACCESSING_OUTER_PRIVATE
		"Virtual or override class \"{0}\" cannot access private methods or fields of the enclosing class."

error	VIRTUAL_CALLING_OUTER_OUTER
		"Virtual or override class \"{0}\" cannot call methods or access fields defined in the enclosing class of its outer class."
