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
