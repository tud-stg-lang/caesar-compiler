package org.caesarj.compiler.export;

import java.io.Serializable;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class AdditionalCaesarTypeInformation implements Serializable {
    private boolean implicit;
    private String[] mixinList;
    private String[] superClasses;
    private String[] superInterfaces;
    private String implClassName;
    
    public AdditionalCaesarTypeInformation(
        boolean implicit,
        String[] mixinList,
        String[] superClasses,
        String[] superInterfaces,
        String implClassName
	) {
        this.implicit = implicit;
        this.mixinList = mixinList;
        this.superClasses = superClasses;
        this.superInterfaces = superInterfaces;
        this.implClassName = implClassName;
    }
    
        
    public String getImplClassName() {
        return implClassName;
    }
    
    public boolean isImplicit() {
        return implicit;
    }
    
    public String[] getMixinList() {
        return mixinList;
    }
    
    public String[] getSuperClasses() {
        return superClasses;
    }
    
    public String[] getSuperInterfaces() {
        return superInterfaces;
    }
}
