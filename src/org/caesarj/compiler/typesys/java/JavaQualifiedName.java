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
		return outerPrefix.replaceAll("\\$", "_")+ident;
	}

	public String getClassName() {
		return className;
	}
}