/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: JavaQualifiedName.java,v 1.6 2005-11-02 12:49:57 thiago Exp $
 */

package org.caesarj.compiler.typesys.java;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class JavaQualifiedName {
    
    private String qualifiedName;
    private String ident;
    private String className;
    private String packagePrefix = "";    
    private String outerPrefix = "";
    private String prefix;
    
    public static final char innerSep   = '$';
    public static final char packageSep = '/';
    
    public JavaQualifiedName(String qualifiedName) {
        int i;
        
        this.qualifiedName = qualifiedName;
        
        i = qualifiedName.lastIndexOf(packageSep);
        if(i >= 0) {
            packagePrefix = qualifiedName.substring(0, i+1);
            ident = qualifiedName.substring(i+1);
        }
        else {
            ident = qualifiedName;
        }
        
        i = ident.lastIndexOf(innerSep);
        if(i >= 0) {
            outerPrefix = ident.substring(0, i+1);
            ident = ident.substring(i+1);
        }
        
        prefix = packagePrefix+outerPrefix;
        className = outerPrefix+ident;
    }
        
    public JavaQualifiedName convertToImplName() {        
        String newQualifiedName = qualifiedName.replaceAll("\\$", "_Impl\\$");
        return new JavaQualifiedName(newQualifiedName+"_Impl");
    }
    
    // CTODO
    public JavaQualifiedName convertToIfcName() {
    	String newQualifiedName = qualifiedName.replaceAll("_Impl", "");
        return new JavaQualifiedName(newQualifiedName);
    }
    
	public char getInnerSep() {
        return innerSep;
    }

    public String getOuterPrefix() {
        return outerPrefix;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public char getPackageSep() {
        return packageSep;
    }

    public String getPrefix() {
        return prefix;
    }
    
    public String getOuterQualifiedName() {
    	if (outerPrefix.equals("")) {
    		return "";
    	}
        return prefix.substring(0, prefix.length()-1);
    }

    public String toString() {
        return qualifiedName;
    }

    public String getIdent() {
        return ident;
    }
    
    
    public int hashCode() {
        return qualifiedName.hashCode();
    }
    
    public boolean equals(Object other) {
    	return qualifiedName.equals(((JavaQualifiedName)other).qualifiedName);
    }

	public String convertToMixinClassName() {
		return ident;
		//return outerPrefix.replaceAll("\\$", "_")+ident;
	}

	public String getClassName() {
		return className;
	}
	
	/**
	 * Converts for exampe:
	 * or.caesarj.test.Collaboration.ExternalizedClass into 
	 * org.caesarj.test.Collaboration_Impl$ExternalizedClass_Impl
	 * @return
	 */
	public JavaQualifiedName convertToExternalizedClassName(){
		StringBuffer newQualifiedName = new StringBuffer();
		String[] packages = qualifiedName.split(String.valueOf(getPackageSep()));
		boolean innerClass = false;
		for(int i = 0; i < packages.length; i++){
			if(!packages[i].equals(packages[i].toLowerCase())){
				if(!innerClass){
					newQualifiedName.append(packages[i] + "_Impl");
					innerClass = true;
				}else{
					newQualifiedName.append(String.valueOf(getInnerSep()) + packages[i] + "_Impl");
				}
			}else{
				newQualifiedName.append(packages[i]);
				newQualifiedName.append(String.valueOf(getPackageSep()));
			}
		}
		//System.out.println("/// JavaQualifiedName : " + newQualifiedName.toString());
		return new JavaQualifiedName(newQualifiedName.toString());
	}

	/**
	 * Replaces the package seperator.
	 * @param newSep
	 * @return
	 */
	public JavaQualifiedName convertPkgSeperator(String newSep){
		String newQualifiedName = qualifiedName.replaceAll(String.valueOf(getPackageSep()), newSep);
        return new JavaQualifiedName(newQualifiedName);
	}
}
