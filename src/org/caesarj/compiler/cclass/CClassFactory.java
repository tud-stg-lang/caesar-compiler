package org.caesarj.compiler.cclass;

import java.util.ArrayList;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMixinInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CClassFactory implements CaesarConstants {

	private CjVirtualClassDeclaration caesarClass;
	
	private String interfaceName;
	private String prefix;
    CClass interfaceOwner;

	private KjcEnvironment environment;
	private TypeFactory typeFactory;

	private TokenReference where;

	/**
	 * Constructor for CaesarDeploymentUtils.
	 */
	public CClassFactory(
		CjVirtualClassDeclaration caesarClass,
		KjcEnvironment environment
    ) {
		this.caesarClass = caesarClass;
		this.where = caesarClass.getTokenReference();
		this.typeFactory = environment.getTypeFactory();
		this.environment = environment;

		initState();
	}

	private void initState() {              
        CCjSourceClass caesarClassOwner = (CCjSourceClass)caesarClass.getOwner();

        if(caesarClassOwner != null) {
            CjVirtualClassDeclaration ownerClassDeclaration = 
                (CjVirtualClassDeclaration)caesarClassOwner.getTypeDeclaration();
            interfaceOwner = 
                ownerClassDeclaration.getMixinIfcDeclaration().getCClass();
        }

        if(interfaceOwner != null) {
            prefix = interfaceOwner.getQualifiedName()+'$';
        }
        else {
            prefix = caesarClass.getCClass().getPackage();
            if(prefix.length() > 0) {
                prefix += '/';
            }
        }

		//Intialize some class and interface identifiers
		interfaceName = caesarClass.getOriginalIdent();
	}

	/**
	 * Creates the cclass Interface.
	 */
	public CjInterfaceDeclaration createCaesarClassInterface() {

		JMethodDeclaration[] cclassMethods = caesarClass.getMethods();

        ArrayList interfaceMethods = new ArrayList(cclassMethods.length);
       
        CReferenceType[] extendedTypes = new CReferenceType[caesarClass.getSuperClasses().length];
        CReferenceType[] implementedTypes = new CReferenceType[caesarClass.getInterfaces().length];
        
        for (int i = 0; i < extendedTypes.length; i++) {
            extendedTypes[i] = caesarClass.getSuperClasses()[i];
        }

        for (int i = 0; i < implementedTypes.length; i++) {
            implementedTypes[i] = caesarClass.getInterfaces()[i];
        }

		CjMixinInterfaceDeclaration cclassInterface =
			new CjMixinInterfaceDeclaration(
				caesarClass.getTokenReference(),
				ACC_PUBLIC,
				interfaceName,
				extendedTypes,
				implementedTypes,
				JFieldDeclaration.EMPTY,
				(JMethodDeclaration[])interfaceMethods.toArray(new JMethodDeclaration[]{}),
				new JTypeDeclaration[0],
				new JPhylum[0]);                  

        cclassInterface._generateInterface(
            environment.getClassReader(), interfaceOwner, prefix
        );

        // link this two AST elements
        caesarClass.setMixinIfcDeclaration(cclassInterface);
        cclassInterface.setCorrespondingClassDeclaration(caesarClass);
        
		return cclassInterface;
	}
        
    private String getPrefix(String qualifiedName) {
        String res = "";

        int i = qualifiedName.lastIndexOf('$');
        
        if(i < 0) {
            i = qualifiedName.lastIndexOf('/'); 
        }
        
        if(i >= 0) {
            res = qualifiedName.substring(0, i+1);
        }
        
		return res;
	}

	public void addCaesarClassInterfaceInners() {        
		caesarClass.getMixinIfcDeclaration().generateInterfaceInners(
            environment.getClassReader(),            
            prefix);            
    }

	private JMethodDeclaration createInterfaceMethod(JMethodDeclaration m) {
		return new CjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_ABSTRACT,
			m.getReturnType(),
			m.getIdent(),
			m.getParameters(),
			m.getExceptions(),
			null,
			null,
			null);

	}
    
	public void modifyCaesarClass() {    	
		caesarClass.setInterfaces(CReferenceType.EMPTY);
		caesarClass.setSuperClass(null);				
		caesarClass.getCClass().setInterfaces(CReferenceType.EMPTY);
		caesarClass.getCClass().setSuperClass(null);
	}
}
