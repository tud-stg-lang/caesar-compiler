/*
 * Created on 24.09.2004
 */
package org.caesarj.runtime.rmi;

import java.io.IOException;
import java.io.ObjectInput;
import java.rmi.server.RemoteRef;

import sun.rmi.server.UnicastServerRef;
import sun.rmi.transport.LiveRef;

/**
 * @author Vaidas Gasiunas
 *
 * Server side remote reference implementation, which takes care of aspect marshalling
 */
public class CjUnicastServerRef extends UnicastServerRef 
{
	public CjUnicastServerRef()
    { }

    public CjUnicastServerRef(int i)
    {
        super(i);
    }
    
    public CjUnicastServerRef(LiveRef liveref)
    {
        super(liveref);
    }
    
    /**
     * Create client side reference on the same object
     */
    protected RemoteRef getClientRef()
    {
    	return new CjUnicastRef(ref);
    }  
	
    /**
     * Unmarshal aspect deployment information
     */
	protected void unmarshalCustomCallData(ObjectInput in)
    	throws IOException, ClassNotFoundException
	{
		AspectMarshalling.unmarshalAspects(in);
	}
}
