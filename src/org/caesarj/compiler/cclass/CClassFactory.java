package org.caesarj.compiler.cclass;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JConstructorDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMemberDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JConstructorCall;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.statement.JConstructorBlock;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
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
	 * Creates the cclass Interface.
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
        
        CReferenceType ifcs[] = caesarClass.getInterfaces();
        
        if(ifcs.length > 0) {
            CReferenceType tmp[] = new CReferenceType[superInterfaces.length+ifcs.length];
            System.arraycopy(superInterfaces, 0, tmp, 0, superInterfaces.length);
            System.arraycopy(ifcs, 0, tmp, superInterfaces.length, ifcs.length);
            superInterfaces = tmp;
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
    
	public void modifyCaesarClass() {    

        {
            CReferenceType superType = caesarClass.getSuperClass();
             
            if(superType != null && !(superType instanceof CCompositeNameType)) {
                CClassNameType newSuperType = new CClassNameType(mapToImplClassName(superType.getQualifiedName()));
                caesarClass.setSuperClass(newSuperType);
            }
        }
        
         JMethodDeclaration methodDecls[] = caesarClass.getMethods();
         boolean defCtorFound = false;
         for (int i = 0; i < methodDecls.length; i++) {             
            if(methodDecls[i] instanceof JConstructorDeclaration) {
                if(methodDecls[i].getParameters().length == 0) {
                    defCtorFound = true;
                    break;
                }
            }
        }
         
        if(!defCtorFound) {
            // create def. ctor -> public CTOR() {super();}
            JConstructorDeclaration defCtor = new JConstructorDeclaration(
                caesarClass.getTokenReference(),
                ACC_PUBLIC,
                caesarClass.getIdent(),
                JFormalParameter.EMPTY,
                CReferenceType.EMPTY,
                new JConstructorBlock(
                    caesarClass.getTokenReference(),
                    new JConstructorCall(
                        caesarClass.getTokenReference(),
                        false,
                        null,
                        JExpression.EMPTY
                    ),
                    JStatement.EMPTY
                ),
                null,
                null,
                environment.getTypeFactory()
            );
            
            caesarClass.addMethod(defCtor);
        }
	}

    // this one will make problems!
    // fullQualifiedName of (e.g.) generated.G$E will be generated/G/E at pre joinAll time 
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
