/*
 * Created on 24.09.2004
 */
package org.caesarj.runtime.rmi;

import java.io.IOException;
import java.io.ObjectOutput;

import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;

/**
 * @author Vaidas Gasiunas
 *
 * Client side remote reference implementation, which takes care of aspect marshalling
 */
public class CjUnicastRef extends UnicastRef
{
	public CjUnicastRef() 
	{ }
	
	public CjUnicastRef(LiveRef ref)
	{
		super(ref);
	}
	
	/**
	 * Marshal aspect deployment information
	 */
	protected void marshalCustomCallData(ObjectOutput out) throws IOException
	{
		AspectMarshalling.marshalAspects(out);
	}
	
	/**
	 *	Ensures correct serialization of the reference 
	 */
	private void writeObject(java.io.ObjectOutputStream out)
		throws java.io.IOException, java.lang.ClassNotFoundException
	{
		writeExternal(out);
	}
	
	/**
	 *	Ensures correct serialization of the reference 
	 */
	private void readObject(java.io.ObjectInputStream in) 
		throws java.io.IOException, java.lang.ClassNotFoundException
	{
		readExternal(in);
	}
	
	/**
	 *	Ensures correct serialization of the reference 
	 */
	public String getRefClass(ObjectOutput objectoutput)
    {
        return null;
    }
}
