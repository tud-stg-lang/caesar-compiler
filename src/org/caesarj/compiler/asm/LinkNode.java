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

import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.internal.ProgramElement;
import org.aspectj.bridge.ISourceLocation;

/**
 * 
 * TODO - Comments
 *
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 *
 */
public class LinkNode extends ProgramElement {
    
    public static final int LINK_NODE_ADVISES = 0;
    public static final int LINK_NODE_ADVISED_BY = 1;
    public static final int LINK_NODE_RELATIONSHIP = 2;
    
    protected IRelationship relationship;
    protected IProgramElement targetElement;
    protected int type;
    
    public LinkNode(IRelationship relationship, IProgramElement targetElement, int type) {
        this.relationship = relationship;
        this.targetElement = targetElement;
        this.type = type;
    }
    
    public LinkNode(IRelationship relationship) {
        this.relationship = relationship;
        this.type = LINK_NODE_RELATIONSHIP;
    }
    
    public ISourceLocation getSourceLocation() {
        return parent.getSourceLocation();
    }

    public Kind getKind() {
        return IProgramElement.Kind.ERROR;
    }
    
    /**
     * @return Returns the relationship.
     */
    public IRelationship getRelationship() {
        return relationship;
    }
    /**
     * @return Returns the target.
     */
    public IProgramElement getTargetElement() {
        return targetElement;
    }
    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }
}
