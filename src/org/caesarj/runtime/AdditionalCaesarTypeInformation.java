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
 * $Id: AdditionalCaesarTypeInformation.java,v 1.2 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.runtime;

import java.io.Serializable;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class AdditionalCaesarTypeInformation implements Serializable {
    private String qn;
    private boolean implicit;
    private String[] mixinList;
    private String[] nestedClasses;
    private String[] incrementFor;
    private String[] superClasses;
    private String[] superInterfaces;
    private String implClassName;
    
    public AdditionalCaesarTypeInformation(
        String qn,
        boolean implicit,
        String[] mixinList,
        String[] nestedClasses,
        String[] incrementFor,
        String[] superClasses,
        String[] superInterfaces,
        String implClassName
    ) {
        this.qn = qn;
        this.implicit = implicit;
        this.mixinList = mixinList;
        this.nestedClasses = nestedClasses;
        this.incrementFor = incrementFor;
        this.superClasses = superClasses;
        this.superInterfaces = superInterfaces;
        this.implClassName = implClassName;
    }
    
    public String getQualifiedName() {
        return qn;
    }    
    public String getImplClassName() {
        return implClassName;
    }
    public boolean isImplicit() {
        return implicit;
    }
    public String[] getIncrementFor() {
        return incrementFor;
    }
    public String[] getMixinList() {
        return mixinList;
    }
    public String[] getNestedClasses() {
        return nestedClasses;
    }
    public String[] getSuperClasses() {
        return superClasses;
    }
    public String[] getSuperInterfaces() {
        return superInterfaces;
    }
    
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append(qn+" "+(implicit?"IMPLICIT":"EXPLICIT"));
        res.append("\n\t     impl name: "+implClassName);
        res.append("\n\t increment for: "+arrayPrint(incrementFor));
        res.append("\n\t super classes: "+arrayPrint(superClasses));
        res.append("\n\t    super ifcs: "+arrayPrint(superInterfaces));
        res.append("\n\t    mixin list: "+arrayPrint(mixinList));
        res.append("\n\tnested classes: "+arrayPrint(nestedClasses));
        res.append('\n');
        return res.toString();
    }
    
    private String arrayPrint(Object[] objs) {
        StringBuffer res = new StringBuffer();
        res.append("[");
        for (int i = 0; i < objs.length; i++) {
            if(i>0) res.append(", ");
            res.append(objs[i]);            
        }
        res.append("]");
        return res.toString();
    }
}
