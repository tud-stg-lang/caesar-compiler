package org.caesarj.compiler.cclass;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.aspectj.CaesarNameMangler;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.*;
import org.caesarj.compiler.ast.phylum.expression.*;
import org.caesarj.compiler.ast.phylum.expression.literal.JIntLiteral;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.expression.literal.JStringLiteral;
import org.caesarj.compiler.ast.phylum.statement.*;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CBinaryClass;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.*;
import org.caesarj.util.TokenReference;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CClassFactory implements CaesarConstants {

	private CjClassDeclaration caesarClass;
	
	private String interfaceName;
	private String packagePrefix;

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

		initNames();
	}

	private void initNames() {
		String packageName = caesarClass.getCClass().getPackage();
		this.packagePrefix = packageName.length() > 0 ? packageName + "/" : "";

		//Intialize some class and interface identifiers
		interfaceName = caesarClass.getOriginalIdent();
	}

	/**
	 * Creates the Aspect Interface.
	 */
	public CjInterfaceDeclaration createCaesarClassInterface() {

		JMethodDeclaration[] cclassMethods = caesarClass.getMethods();

        ArrayList interfaceMethods = new ArrayList(cclassMethods.length);

		for (int i = 0; i < cclassMethods.length; i++) {      
            if(
                !(cclassMethods[i] instanceof JConstructorDeclaration)
                && ((cclassMethods[i].getModifiers() & JMemberDeclaration.ACC_PUBLIC) != 0)
            )      
                interfaceMethods.add(createInterfaceMethod(cclassMethods[i]));
		}

        CReferenceType[] superInterfaces = new CReferenceType[]{};
        
        CReferenceType superType = caesarClass.getSuperClass();
        
        // hack
        if(superType instanceof CClassNameType) {            
            superInterfaces = new CReferenceType[]{superType};
        }

		CjInterfaceDeclaration cclassInterface =
			new CjInterfaceDeclaration(
				caesarClass.getTokenReference(),
				ACC_PUBLIC,
				interfaceName,
				CTypeVariable.EMPTY,
				superInterfaces,
				JFieldDeclaration.EMPTY,
				(JMethodDeclaration[])interfaceMethods.toArray(new JMethodDeclaration[]{}),
				new JTypeDeclaration[0],
				new JPhylum[0],
				null,
				null);

		cclassInterface.generateInterface(
			environment.getClassReader(),
			caesarClass.getOwner(),
			packagePrefix);

		return cclassInterface;
	}

	/**
	 * Creates an advice method for the aspect interface.
	 */
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
        
        // hack
        if(superType instanceof CClassNameType) {
            CClassNameType newSuperType = new CClassNameType(superType.getQualifiedName()+"_Impl");
            caesarClass.setSuperClass(newSuperType);
        }
	}

}
