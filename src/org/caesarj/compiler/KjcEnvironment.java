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
 * $Id: KjcEnvironment.java,v 1.4 2004-06-02 15:03:13 aracic Exp $
 */

package org.caesarj.compiler;

import org.caesarj.compiler.cclass.CaesarTypeGraph;
import org.caesarj.compiler.cclass.CaesarTypeSystem;
import org.caesarj.compiler.types.SignatureParser;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;

/**
 * Environment of the Kjc Compiler
 */

public class KjcEnvironment {

	public KjcEnvironment(
		ClassReader classReader,
		TypeFactory typeFactory,
		KjcOptions options) {
		this.classReader = classReader;
		this.typeFactory = typeFactory;
		this.options = options;
		this.languageExtensions = new LanguageExtensions();
        this.caesarTypeSystem = new CaesarTypeSystem();
	}

	public ClassReader getClassReader() {
		return classReader;
	}
    
    public CaesarTypeSystem getCaesarTypeSystem() {
        return caesarTypeSystem;
    }

	public TypeFactory getTypeFactory() {
		return typeFactory;
	}

	public SignatureParser getSignatureParser() {
		return classReader.getSignatureParser();
	}

	public LanguageExtensions getLanguageExtFactory() {
		return languageExtensions;
	}

	public int getSourceVersion() {
		if (options.source.equals("1.1")) {
			return SOURCE_1_1;
		}
		else if (options.source.equals("1.2")) {
			return SOURCE_1_2;
		}
		else if (options.source.equals("1.3")) {
			return SOURCE_1_3;
		}
		else if (options.source.equals("1.4")) {
			return SOURCE_1_4;
		}
		else {
			throw new InconsistencyException("Wrong source language in options");
		}
	}

	public boolean isGenericEnabled() {
		return options.generic;
	}
    
	private final ClassReader classReader;
    private final CaesarTypeSystem caesarTypeSystem;
    private final TypeFactory typeFactory;
	private final KjcOptions options;
	private final LanguageExtensions languageExtensions;

	public final static int SOURCE_1_1 = 101;
	public final static int SOURCE_1_2 = 102;
	public final static int SOURCE_1_3 = 103;
	public final static int SOURCE_1_4 = 104;
}
