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
 */

package org.caesarj.compiler.asm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.aspectj.asm.AsmManager;
import org.aspectj.asm.IHierarchy;
import org.aspectj.asm.IRelationshipMap;
import org.aspectj.asm.internal.RelationshipMap;

/**
 * TODO - Comments
 *
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 *
 */
public class CaesarJAsmManager {
    
    protected IHierarchy hierarchy;
    protected IRelationshipMap map;

    /**
     * Creates a new CaesarJAsmManager attaching its hierarchy and map to aspectj's
     * AsmManager singleton instances.
     * 
     * This is needed before building the model, because the weaver uses the AsmManager
     * singleton.
     * 
     * @see deattach()
     */
    public CaesarJAsmManager() {
        this.hierarchy = AsmManager.getDefault().getHierarchy();
        this.map = AsmManager.getDefault().getRelationshipMap();
    }
    
    /**
     * This method makes clones from the hierarchy and map and set this object's
     * attribute to refer to the new clones.
     * After calling this method, this object will be deattached from the AsmManager
     * singleton and its hierarchy and map will never be built again.
     */
    public void deattach() {
        
        try {
            // This is one of the ugliest things I've ever seen, but there's no way to clone
            // the hierarchy and the map from the AsmManager without changing it, because it
            // has only private members and only public getters.
            // But it works...
            
            // Creates a byte array and write the hierarchy and map to it
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(hierarchy);
            out.writeObject(map);
            
            // Creates a byte array reader and read the hierarchy and map from it
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            IHierarchy newHierarchy = (IHierarchy) in.readObject();
            IRelationshipMap newRelationshipMap = (IRelationshipMap) in.readObject();
                
            // Attach the new objects to this manager
            this.hierarchy = newHierarchy;
            this.map = newRelationshipMap;
            
            // Since the hierarchy is transient, we have to set it
            ((RelationshipMap) this.map).setHierarchy(hierarchy);
            
        } catch (Exception e) {
            e.printStackTrace();
            this.hierarchy = null;
            this.map = null;
        }
    }
    
    /**
     * @return Returns the hierarchy.
     */
    public IHierarchy getHierarchy() {
        return hierarchy;
    }
    /**
     * @return Returns the relationship map.
     */
    public IRelationshipMap getRelationshipMap() {
        return map;
    }
}
