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
 * $Id: KjcMessages.msg,v 1.101 2002/07/09 17:28:13 lackner Exp $
 */

package caesar.ci.compiler

prefix Cci
parent at.dms.compiler.CompilerMessages

// CLS messages
error   PROVIDED_METHOD_FLAGS
        "Provided method flags in \"{0}\" must be some of: provided, public, abstract"
        "CLS 1.1"

error   EXPECTED_METHOD_FLAGS
        "Expected method flags in \"{0}\" must be some of: expected, public, abstract"
		"CLS 1.1"


error   PROVIDED_METHOD_OUT_CI
		"Provided method \"{0}\" must be defined in a collaboration interaface."
		"CLS 1.1"

error   EXPECTED_METHOD_OUT_CI
		"Expected method \"{0}\" must be defined in a collaboration interaface."
		"CLS 1.1"

error   COLLABORATION_INTERFACE_METHOD_FLAGS
		"Method \"{0}\" must be provided or expected."
		"CLS 1.1"

error   NOT_CI
		"Interface \"{0}\" must be a collaboration interface."
		"CLS 1.1"

error   EXPECTED_METHOD_IN_IMPLEMENTATION
		"Expected method \"{0}\" cannot be implemented in an implementation class."
		"CLS 1.1"
		
error   PROVIDED_METHOD_IN_BINDING
		"Provided method \"{0}\" cannot be implemented in a binding class."
		"CLS 1.1"
		