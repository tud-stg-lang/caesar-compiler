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
 * $Id: CjUnicastRef.java,v 1.2 2005-01-24 16:52:59 aracic Exp $
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
