package org.caesarj.compiler.cclass;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.*;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JUnqualifiedInstanceCreation;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.*;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

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
		//caesarClass.addInterface(new CClassNameType(interfaceName));
            
        CReferenceType superType = caesarClass.getSuperClass();
         
        if(superType != null && !(superType instanceof CCompositeNameType)) {
            CClassNameType newSuperType = new CClassNameType(mapToImplClassName(superType.getQualifiedName()));
            caesarClass.setSuperClass(newSuperType);
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


    /**
     * for all constructos C_n(c_n1, ..., c_nm) {...} 
     * -> $newX(c_n1, ... c_nm) to owner class
     * 
     * CTODO addCreateMethodsToOwnerClass
     * - exceptions
     */
	public void addCreateMethodsToOwnerClass() throws UnpositionedError {
        CClass clazz         = caesarClass.getCClass();
        CCjSourceClass owner = (CCjSourceClass)clazz.getOwner();

        if(owner == null) return;
       
        CjClassDeclaration ownerClassDecl = (CjClassDeclaration)owner.getTypeDeclaration();//caesarClass.getOwnerDeclaration();        
        
        // collect constructors
        JMethodDeclaration methods[] = caesarClass.getMethods();
        
        for(int i=0; i<methods.length; i++) {            
            if(!(methods[i] instanceof JConstructorDeclaration)) continue; 
            
			JMethodDeclaration ctor = methods[i];            

            JFormalParameter[] ctorParams = ctor.getParameters();
            JFormalParameter formalParams[] = new JFormalParameter[ctorParams.length];
                       
            JExpression params[] = new JExpression[ctorParams.length];
            
            for(int j=0; j<ctorParams.length; j++) {
                params[j] = new JNameExpression(
                    caesarClass.getTokenReference(),
                    null,
                    ctorParams[j].getIdent()
                );
            }
                       
            JStatement returnStatement = new JReturnStatement(
                caesarClass.getTokenReference(),
                new JUnqualifiedInstanceCreation(
                    caesarClass.getTokenReference(),
                    clazz.getAbstractType(),
                    params
                ),
                null
            ); 
                       
            JBlock body = new JBlock(
                caesarClass.getTokenReference(),
                new JStatement[]{returnStatement},
                null
            );
                                 
            for(int j=0; j<ctorParams.length; j++) {
                 formalParams[j] = new JFormalParameter(
                    caesarClass.getTokenReference(),
                    JFormalParameter.DES_PARAMETER,
                    ctorParams[j].getType(),
                    ctorParams[j].getIdent(),
                    ctorParams[j].isFinal()
                 );
            } 
            
            CClass topmostInterfaceType =
                clazz.getInterfaces()[0].getCClass().getTopmostHierarchyInterface();

            CReferenceType returnType = 
                new CClassNameType(topmostInterfaceType.getOwner().getIdent()+'/'+caesarClass.getOriginalIdent());
                        
            JMethodDeclaration methodDecl = new JMethodDeclaration(
                caesarClass.getTokenReference(),
                ACC_PUBLIC,                
                CTypeVariable.EMPTY,                
                returnType,
                "$new"+caesarClass.getOriginalIdent(),
                formalParams,
                CReferenceType.EMPTY,
                body,
                null,
                null
            );
            
            JMethodDeclaration interfaceMethodDecl = new JMethodDeclaration(
                caesarClass.getTokenReference(),
                ACC_PUBLIC | ACC_ABSTRACT,                
                CTypeVariable.EMPTY,                
                returnType,
                "$new"+caesarClass.getOriginalIdent(),
                formalParams,
                CReferenceType.EMPTY,
                null,
                null,
                null
            );
            
            ownerClassDecl.addMethod(methodDecl);
            ownerClassDecl.getCorrespondingInterfaceDeclaration().addMethod(interfaceMethodDecl);
		}
	}
    

}
