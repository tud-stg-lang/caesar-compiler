package org.caesarj.compiler.cclass;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class JavaQualifiedName {
    
    private String qualifiedName;
    private String ident;
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
}
