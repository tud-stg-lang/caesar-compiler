package org.caesarj.compiler.cclass;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.*;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CCompositeNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CClassFactory implements CaesarConstants {

	private CjClassDeclaration caesarClass;
	
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
		CjClassDeclaration caesarClass,
		KjcEnvironment environment) {
		super();

		this.caesarClass = caesarClass;
		this.where = caesarClass.getTokenReference();
		this.typeFactory = environment.getTypeFactory();
		this.environment = environment;

		initState();
	}

	private void initState() {              
        CCjSourceClass caesarClassOwner = (CCjSourceClass)caesarClass.getOwner();

        if(caesarClassOwner != null) {
            CjClassDeclaration ownerClassDeclaration = 
                (CjClassDeclaration)caesarClassOwner.getTypeDeclaration();
            interfaceOwner = 
                ownerClassDeclaration.getCorrespondingInterfaceDeclaration().getCClass();
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
	 * Creates the Aspect Interface.
	 */
	public CjInterfaceDeclaration createCaesarClassInterface() {

		JMethodDeclaration[] cclassMethods = caesarClass.getMethods();

        ArrayList interfaceMethods = new ArrayList(cclassMethods.length);

        // copy all public, non-static class methods to interface
		for (int i = 0; i < cclassMethods.length; i++) {      
            if(
                !(cclassMethods[i] instanceof JConstructorDeclaration)
                && ((cclassMethods[i].getModifiers() & JMemberDeclaration.ACC_PUBLIC) != 0)
                && ((cclassMethods[i].getModifiers() & JMemberDeclaration.ACC_STATIC) == 0)
            )      
                interfaceMethods.add(createInterfaceMethod(cclassMethods[i]));
		}

        // default is our interface has no superinterface
        CReferenceType[] superInterfaces = new CReferenceType[]{};
        
        CReferenceType superType = caesarClass.getSuperClass();
        
        // CTODO think about it
        if(superType instanceof CCompositeNameType) {
            // if we have a composite type our superinterface list consists
            // of composite type's typeList 
            CCompositeNameType compositType = (CCompositeNameType)superType;
            CClassNameType typeList[] = compositType.getTypeList();
            superInterfaces = new CReferenceType[typeList.length];            
            for(int i=0; i<typeList.length; i++) {
                superInterfaces[i] = typeList[i];
            }
        }
        else if(superType instanceof CClassNameType) {
            // if we have a super cclass, our superinterface list consist of
            // superTypes interface name
            superInterfaces = new CReferenceType[]{superType};
        }
        

		CjInterfaceDeclaration cclassInterface =
			new CjInterfaceDeclaration(
				caesarClass.getTokenReference(),
				ACC_PUBLIC | ACC_CCLASS_INTERFACE,
				interfaceName,
				CTypeVariable.EMPTY,
				superInterfaces,
				JFieldDeclaration.EMPTY,
				(JMethodDeclaration[])interfaceMethods.toArray(new JMethodDeclaration[]{}),
				new JTypeDeclaration[0],
				new JPhylum[0],
				null,
				null);                  

        cclassInterface._generateInterface(
            environment.getClassReader(), interfaceOwner, prefix
        );

        // link this two AST elements
        caesarClass.setCorrespondingInterfaceDeclaration(cclassInterface);
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
        CjInterfaceDeclaration cclassInterface = caesarClass.getCorrespondingInterfaceDeclaration();
        cclassInterface.generateInterfaceInners(
            environment.getClassReader(),            
            prefix);            
    }

	private JMethodDeclaration createInterfaceMethod(JMethodDeclaration m) {
		return new CjMethodDeclaration(
			where,
			ACC_PUBLIC | ACC_ABSTRACT,
			CTypeVariable.EMPTY,
			m.getReturnType(),
			m.getIdent(),
			m.getParameters(),
			m.getExceptions(),
			null,
			null,
			null);

	}

	/**
	 * Modifes the aspect class.
	 * Makes it implement the aspectInterface, adds the required methods 
	 * and adds a private deploymentThread field.
	 */
	public void modifyCaesarClass() {

		caesarClass.addInterface(
			new CClassNameType(interfaceName));
            
        CReferenceType superType = caesarClass.getSuperClass();
         
        if(superType != null && !(superType instanceof CCompositeNameType)) {
            CClassNameType newSuperType = new CClassNameType(mapToImplClassName(superType.getQualifiedName()));
            caesarClass.setSuperClass(newSuperType);
        }
	}

    // this one will make problems!
    // fullQualifiedName of (e.g.) generated.G$E will be generated/G/E 
    private String mapToImplClassName(String fullQualifiedName) {
        StringBuffer res = new StringBuffer();
        StringTokenizer tok = new StringTokenizer(fullQualifiedName, "/");
        
        while(tok.hasMoreTokens()) {
            String token = tok.nextToken();
            res.append(token);
            res.append("_Impl");
            if(tok.hasMoreTokens())
                res.append('/');
        }
        
        return res.toString();
    }

}
