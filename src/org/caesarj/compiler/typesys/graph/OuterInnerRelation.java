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
 * $Id: OuterInnerRelation.java,v 1.4 2005-01-24 16:53:02 aracic Exp $
 */

package org.caesarj.compiler.typesys.graph;

/**
 * Representes an outer <-> inner class relation
 * 
 * @author Ivica Aracic
 */
public class OuterInnerRelation extends BidirectionalRelation {
	public OuterInnerRelation(CaesarTypeNode outerNode, CaesarTypeNode innerNode) {
		this(false, outerNode, innerNode);
	}

	public OuterInnerRelation(boolean implicit, CaesarTypeNode outerNode, CaesarTypeNode innerNode) {
		super(implicit, outerNode, innerNode);
		outerNode.addEnclosingFor(this);
		innerNode.addEnclosedBy(this);
	}
		
	public CaesarTypeNode getOuterNode() {
		return getFirst();
	}

	public CaesarTypeNode getInnerNode() {
		return getSecond();
	}
}
