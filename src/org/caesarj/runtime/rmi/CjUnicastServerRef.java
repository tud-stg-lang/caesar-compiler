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
 * $Id: CjUnicastServerRef.java,v 1.2 2005-01-24 16:52:59 aracic Exp $
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
