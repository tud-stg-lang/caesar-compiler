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
 * $Id: CaesarExtraAttributes.java,v 1.3 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.classfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.caesarj.runtime.AdditionalCaesarTypeInformation;
import org.caesarj.util.InconsistencyException;

/**
 * Adapter for caesar attributes
 * 
 * @author Ivica Aracic
 */
public class CaesarExtraAttributes {

    /** Name constants */
    
	public final static String
		ATTRIB_PREFIX = "org.caesarj.",
		ATTRIB_EXTRA_MODIFIERS = ATTRIB_PREFIX + "ExtraModifiers",
		ATTRIB_EXTRA_TYPEINFO = ATTRIB_PREFIX + "TypeInfo";
	
	/** The binary mask for the modifiers to store */
	public final static int		
		EXTRA_MOD_MASK	= ~0xFFF;
    
	public static int readExtraModifiers(GenericAttribute ga) throws IOException {
        assertAttributeName(ga, ATTRIB_EXTRA_MODIFIERS);
        return ((Integer)read(ga.getData())).intValue();        
    }
    
    public static AdditionalCaesarTypeInformation readAdditionalTypeInfo(GenericAttribute ga) throws IOException {
        assertAttributeName(ga, ATTRIB_EXTRA_TYPEINFO);
        return (AdditionalCaesarTypeInformation)read(ga.getData());
    }
    
    public static GenericAttribute writeExtraModifiers(int modifiers) throws IOException {
        return new GenericAttribute(ATTRIB_EXTRA_MODIFIERS, write(new Integer(modifiers)));
    }
    
    public static GenericAttribute writeAdditionalTypeInfo(AdditionalCaesarTypeInformation info) throws IOException {
        return new GenericAttribute(ATTRIB_EXTRA_TYPEINFO, write(info));
    }
    
    private static void assertAttributeName(GenericAttribute ga, String name) {
        if(!ga.getName().equals(name))
            throw new InconsistencyException("invalid attribute");
    }

    private static byte[] write(Object obj) throws IOException {
	    byte[] res;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream o = new ObjectOutputStream(out);
	    o.writeObject(obj);
	    res = out.toByteArray();
	    o.close();	    
	    return res;
	}
	
    private static Object read(byte[] data) throws IOException {
        try {
		    ByteArrayInputStream in = new ByteArrayInputStream(data);
		    ObjectInputStream i = new ObjectInputStream(in);
		    Object res = i.readObject();	    
		    i.close();	    
		    return res;
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new InconsistencyException(e.getMessage());
        }
	}
}
