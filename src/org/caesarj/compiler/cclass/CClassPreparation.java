/*
 * Created on 08.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.caesarj.compiler.cclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JConstructorDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JConstructorCall;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JUnqualifiedInstanceCreation;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JConstructorBlock;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CCompilationUnitContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * ...
 * 
 * @author Ivica
 */
public class CClassPreparation implements CaesarConstants {

    private static CClassPreparation singleton = new CClassPreparation();
    
    public static CClassPreparation instance() {
        return singleton; 
    }
    
    private CClassPreparation() {
    }

    /**     
     * This step is done before joinAll step
     * 
     * - generate cclass interface
     * - add implements cclass interface
     * - rename types in extends to *_Impl
     */    
    public void prepareCaesarClass(
        KjcEnvironment environment,
        JCompilationUnit cu
    ) {
        prepareCaesarClass(environment, new CompilationUnitInnerAccessor(cu));
    }

	private void prepareCaesarClass(
		KjcEnvironment environment,
		InnerAccessor innerAccessor
    ) {
        List newTypeDeclarations = new ArrayList();
        JTypeDeclaration typeDeclarations[] = innerAccessor.getInners();
        
        for (int i = 0; i < typeDeclarations.length; i++) {
            if (typeDeclarations[i] instanceof CjClassDeclaration) {

                CjClassDeclaration caesarClass =
                    (CjClassDeclaration) typeDeclarations[i];

                CClassFactory factory =
                    new CClassFactory(caesarClass, environment);

                // create class interface                           
                newTypeDeclarations.add(factory.createCaesarClassInterface());
        
                // add implements cclass interface and rename supertype to *_Impl  
                factory.modifyCaesarClass();

                if (caesarClass.getInners().length > 0) {
                    //consider nested types
                    prepareCaesarClass(
                        environment, new ClassDeclarationInnerAccessor(caesarClass));
                }
                
                // this has to be called after inner types have been handled
                // (for creating cclass interface inner source class exports)
                factory.addCaesarClassInterfaceInners();
            }
        }


        innerAccessor.addInners(
            (JTypeDeclaration[]) newTypeDeclarations.toArray(
                new JTypeDeclaration[0]));             
	}
   
    
    /**
     * virtual classes automatically inherit the ctors of super class
     */
    public void checkConstructorInheritance(
        CompilerBase compilerBase, 
        KjcEnvironment environment
    ) {
        CaesarTypeSystem caesarTypeSystem = environment.getCaesarTypeSystem(); 
        TypeFactory typeFactory = environment.getTypeFactory();
        ClassReader classReader = environment.getClassReader();
                
        Collection allTypes = caesarTypeSystem.getJavaGraph().getAllTypes();        
        
        for (Iterator it = allTypes.iterator(); it.hasNext();) {
            /*
             * add inherited ctor exports to cclass
             */
            JavaTypeNode item = (JavaTypeNode)it.next();            
            JavaTypeNode parent = item.getParent();
            
            if(item.isRoot()) continue;            
            if(parent.isRoot()) continue;
            
            CjClassDeclaration decl = item.getDeclaration();
            
            CClass itemClass = item.getCClass();            
            CClass parentClass = parent.getCClass();
            
            Collection parentCtors = new LinkedList();
            Collection itemCtors = new LinkedList();
            int ctorInherited = 0;
            
            // collect ctors of item class
            for(int i=0; i<itemClass.getMethods().length; i++) {
                if(itemClass.getMethods()[i].isConstructor())
                    itemCtors.add(itemClass.getMethods()[i]);
            }
            
            // collect ctors of parent class
            for(int i=0; i<parentClass.getMethods().length; i++) {
                if(parentClass.getMethods()[i].isConstructor())
                    parentCtors.add(parentClass.getMethods()[i]);
            }
            
            
            // check ctor inheritance 
            for (Iterator it1 = parentCtors.iterator(); it1.hasNext();) {
                CMethod parentCtor = (CMethod) it1.next();
                boolean foundInSubClass = false;
                for (Iterator it2 = itemCtors.iterator(); it2.hasNext() && !foundInSubClass;) {
                    CMethod itemCtor = (CMethod) it2.next();
                    
                    if(parentCtor.isEqualSignature(itemCtor))
                        foundInSubClass = true;
                }
                
                // add it if not found in item
                if(!foundInSubClass) {       
                    ctorInherited++;
                    
                    JConstructorBlock body = null;
                    
                    // create ctor body if item has declaration
                    if(decl != null) {
                        
                        JExpression[] params = new JExpression[parentCtor.getParameters().length];
                        for (int i = 0; i < params.length; i++) {
                            params[i] = new JNameExpression(decl.getTokenReference(), ("p"+i).intern());
                        }
                        
                        body = new JConstructorBlock(
                            decl.getTokenReference(),
                            new JConstructorCall(
                                decl.getTokenReference(),
                                false,
                                null,
                                params
                            ),
                            JStatement.EMPTY
                        );
                    }
                    
                    CSourceMethod inheritedCtor = new CSourceMethod(
                        itemClass,
                        parentCtor.getModifiers(),
                        JAV_CONSTRUCTOR,
                        typeFactory.getVoidType(),
                        parentCtor.getParameters(),
                        parentCtor.getThrowables(),
                        parentCtor.getTypeVariables(),
                        parentCtor.isDeprecated(),
                        parentCtor.isSynthetic(),
                        body
                    );
                    
                    itemClass.addMethod(inheritedCtor); // CTODO performance
             
                    // if item has a declaration add also JConstructorDeclarations
                    if(decl != null) {
                        
                        JFormalParameter formalParams[] = new JFormalParameter[parentCtor.getParameters().length];
                        
                        for (int i = 0; i < formalParams.length; i++) {
                            formalParams[i] = new JFormalParameter(
                                decl.getTokenReference(),
                                JFormalParameter.DES_PARAMETER,
                                parentCtor.getParameters()[i],
                                ("p"+i).intern(),
                                false // CTODO: final markierte ctor params                                
                            );
                        }
                        
                        JMethodDeclaration inheritedCtorDecl = new JConstructorDeclaration(
                            decl.getTokenReference(),
                            parentCtor.getModifiers(),
                            item.getQualifiedName().getIdent(),
                            formalParams,
                            parentCtor.getThrowables(),
                            body,
                            null, null,
                            typeFactory
                        );
                        
                        decl.addMethod(inheritedCtorDecl); // CTODO performance
                    }
                }
            }
                
            // add default ctor if necessery
            if(itemCtors.size() + ctorInherited == 0) {
                
                TokenReference srcRef = decl != null ? decl.getTokenReference() : TokenReference.NO_REF;
                
                JConstructorBlock body = null;
                
                if(decl != null) {       
                    body = new JConstructorBlock(
                        srcRef,
                        new JConstructorCall(
                            srcRef,
                            false,
                            null, // expr
                            JExpression.EMPTY
                        ),
                        JStatement.EMPTY
                    );
                
                    JConstructorDeclaration defCtorDecl = new JConstructorDeclaration(
                        srcRef,
                        ACC_PUBLIC,
                        item.getQualifiedName().getIdent(),
                        JFormalParameter.EMPTY,
                        CReferenceType.EMPTY,
                        body,
                        null, null,
                        typeFactory
                    );     
                    
                    decl.addMethod(defCtorDecl);
                }
                
                CSourceMethod defCtor = new CSourceMethod(
                    itemClass,
                    ACC_PUBLIC,
                    JAV_CONSTRUCTOR,
                    typeFactory.getVoidType(),
                    CReferenceType.EMPTY,
                    CReferenceType.EMPTY,
                    CTypeVariable.EMPTY,
                    false,
                    false,
                    body
                );
                
                itemClass.addMethod(defCtor);
            }
        }
    }
    
    
    /**
     * for each inner class which corresponds to a caesar type,
     * we generatea for each ctor of this inner class a factory method in the owner type
     */
    public void generateFactoryMethods(
        CompilerBase compilerBase, 
        KjcEnvironment environment
    ) throws UnpositionedError {
        try {
            CaesarTypeSystem caesarTypeSystem = environment.getCaesarTypeSystem(); 
            TypeFactory typeFactory = environment.getTypeFactory();
            ClassReader classReader = environment.getClassReader();
                    
            Collection allTypes = caesarTypeSystem.getJavaGraph().getAllTypes();        
            
            for (Iterator it = allTypes.iterator(); it.hasNext();) {
                JavaTypeNode item = (JavaTypeNode)it.next();                        
                            
                CjClassDeclaration decl = item.getDeclaration();
                if(decl != null) {
                    CjInterfaceDeclaration ifcDecl = decl.getCorrespondingInterfaceDeclaration();
                    for (Iterator innerIt = item.getInners().iterator(); innerIt.hasNext();) {
                        JavaTypeNode inner = (JavaTypeNode) innerIt.next();
                        
                        // if we have a inner corresponding to a caesar type generate factory method
                        if(!inner.isToBeGeneratedInBytecode()) {                        
                            CClass innerClass = inner.getCClass();
                            CMethod methods[] = innerClass.getMethods();
                            
                            for (int i = 0; i < methods.length; i++) {
                                if(methods[i].isConstructor()) {
                                    
                                    JExpression[] params = new JExpression[methods[i].getParameters().length];
                                    for (int j = 0; j < params.length; j++) {
                                        params[j] = new JNameExpression(decl.getTokenReference(), ("p"+j).intern());
                                    }
                                    
                                    JBlock creationBlock = new JBlock(
                                        decl.getTokenReference(),
                                        new JStatement[] {
                                            new JReturnStatement(
                                                decl.getTokenReference(),
                                                new JUnqualifiedInstanceCreation(
                                                    decl.getTokenReference(),
                                                    innerClass.getAbstractType(),
                                                    params
                                                ),
                                                null
                                            )
                                        },
                                        null
                                    );
                                    
                                    JFormalParameter formalParams[] = 
                                        new JFormalParameter[methods[i].getParameters().length];
                                    
                                    for (int j = 0; j < formalParams.length; j++) {
                                        formalParams[j] = new JFormalParameter(
                                            decl.getTokenReference(),
                                            JFormalParameter.DES_PARAMETER,
                                            methods[i].getParameters()[j],
                                            ("p"+j).intern(),
                                            false // CTODO: final markierte ctor params                                
                                        );
                                    }
                                    
                                        
                                    CaesarTypeNode topMost = inner.getMixin().getTopmostNode();
                                    CClass returnType = classReader.loadClass(
                                        typeFactory, 
                                        topMost.getQualifiedName().toString()    
                                    );
                                    
                                    JMethodDeclaration facMethodDecl = new JMethodDeclaration(
                                            decl.getTokenReference(),
                                            methods[i].getModifiers(),
                                            methods[i].getTypeVariables(),
                                            returnType.getAbstractType(),
                                            "$new"+inner.getType().getQualifiedName().getIdent(),
                                            formalParams, // formal params
                                            methods[i].getThrowables(),
                                            creationBlock,
                                            null, null
                                        );  
                                    
                                    JMethodDeclaration facIfcMethodDecl = new JMethodDeclaration(
                                        decl.getTokenReference(),
                                        methods[i].getModifiers(),
                                        methods[i].getTypeVariables(),
                                        returnType.getAbstractType(),
                                        "$new"+inner.getType().getQualifiedName().getIdent(),
                                        formalParams, // formal params
                                        methods[i].getThrowables(),
                                        null,
                                        null, null
                                    );  
                                    
                                    decl.addMethod(facMethodDecl);
                                    ifcDecl.addMethod(facIfcMethodDecl);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            // MSG
            throw new UnpositionedError(CaesarMessages.CANNOT_CREATE);
        }
    }
    

    /**
     * This step is done after types has been joined
     * 
     * We are generating here missing mixin chain parts
     */
    public void generateMissingMixinChainParts(
        CompilerBase compilerBase, 
        KjcEnvironment environment,
        JCompilationUnit cu
    ) {
        CaesarTypeSystem caesarTypeSystem = environment.getCaesarTypeSystem(); 
        TypeFactory typeFactory = environment.getTypeFactory();
        ClassReader classReader = environment.getClassReader();
        
        CCompilationUnitContext context =
            new CCompilationUnitContext(compilerBase, environment, cu.getExport());
        
        Collection typesToGenerate = caesarTypeSystem.getJavaGraph().getTypesToGenerate();        
        
        for (Iterator it = typesToGenerate.iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
                        
            CClass clazz = generateCClass(item, context);
            item.setCClass(clazz);            
        }
    }
    
    public CClass generateCClass(JavaTypeNode node, CContext context) {
        // CTODO we need binary/source flag for CaesarTypeNode
        // for now consider everything as non-binary
        try {        
            
            if(node.getParent() == null) {
                return 
                    context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT).getCClass();
            }
            
            
            if(context.getClassReader().hasClassFile(node.getQualifiedName().toString())) {
                return
                    context.getClassReader().loadClass(
                        context.getTypeFactory(),
                        node.getQualifiedName().toString()
                    );
            }
            
            // get mixin export
            CReferenceType mixinType = 
                context.getTypeFactory().createType(node.getMixin().getQualifiedImplName().toString(), false);
            
            mixinType = (CReferenceType)mixinType.checkType(context);
            CClass mixinClass = mixinType.getCClass();

            // find owner
            CClass owner = null;
            JavaTypeNode outer = node.getOuter();
            if(outer != null) {                
                owner = generateCClass(outer, context);
            }
                        
            CSourceClass sourceClass = new CSourceClass(
                owner,
                TokenReference.NO_REF, // CTODO token reference for generated source classes?
                mixinClass.getModifiers(),
                node.getQualifiedName().getIdent(),
                node.getQualifiedName().toString(),
                CTypeVariable.EMPTY,
                false, // deprecated?
                false, // synthetic?
                null // CTODO: declaration unit is null?
            );

            
            // generate super type
            JavaTypeNode itemParent = node.getParent();

            CReferenceType superClass = 
                generateCClass(itemParent, context).getAbstractType();
            
            superClass = (CReferenceType)superClass.checkType(context);
            
            
            // generate methods
            CMethod mixinMethods[] = mixinClass.getMethods();
            CMethod methods[] = new CMethod[mixinMethods.length];
            for (int i = 0; i < methods.length; i++) {
                methods[i] = new CSourceMethod(
                    sourceClass,
                    mixinMethods[i].getModifiers(),
                    mixinMethods[i].getIdent(),
                    mixinMethods[i].getReturnType(),
                    mixinMethods[i].getParameters(),
                    mixinMethods[i].getThrowables(),
                    mixinMethods[i].getTypeVariables(),
                    mixinMethods[i].isDeprecated(),
                    mixinMethods[i].isSynthetic(),
                    null // CTODO JBlock is null?
                );
            }
            
            
            // generate fields
            Hashtable fields = new Hashtable();
            CField mixinFields[] = mixinClass.getFields();
            for(int i=0; i<mixinFields.length; i++) {
                CSourceField clone = new CSourceField(
                    sourceClass,
                    mixinFields[i].getModifiers(),
                    mixinFields[i].getIdent(),
                    mixinFields[i].getType(),
                    mixinFields[i].isDeprecated(),
                    mixinFields[i].isSynthetic()
                );
                
                clone.setPosition(i);
                fields.put(clone.getIdent(), clone);
            }
            
            // generate interfaces
            CReferenceType interfaces[] = new CReferenceType[]{mixinClass.getAbstractType()};
            
            sourceClass.setInnerClasses(new CReferenceType[0]);
            
            sourceClass.close(interfaces, superClass, fields, methods);
                        
            
            // add to class reader
            context.getClassReader().addSourceClass(sourceClass);
            
            return sourceClass;
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    /*
     * 
     * INNER ACCESSORS
     * 
     */
    
    /**
     * Offers common access interface for cu and class inners
     *  
     * @author Ivica Aracic
     */    
    interface InnerAccessor {
        JTypeDeclaration[] getInners();
        void addInners(JTypeDeclaration[] inners);
    }
    
    
    class ClassDeclarationInnerAccessor implements InnerAccessor {        
        CjClassDeclaration cd;        
        
        ClassDeclarationInnerAccessor(CjClassDeclaration cd) {
            this.cd = cd;
        }
        
		public JTypeDeclaration[] getInners() {
			return cd.getInners();
		}

        public void addInners(JTypeDeclaration[] inners) {
            cd.getCorrespondingInterfaceDeclaration().setInners(inners);
        }
    }
    
    
    class CompilationUnitInnerAccessor implements InnerAccessor {
        JCompilationUnit cu;       
        
        CompilationUnitInnerAccessor(JCompilationUnit cu) {
            this.cu = cu;
        }

        public JTypeDeclaration[] getInners() {
            return cu.getInners();
        }

        public void addInners(JTypeDeclaration[] inners) {
            JTypeDeclaration cuInners[] = cu.getInners(); 
            JTypeDeclaration newInners[] = new JTypeDeclaration[cuInners.length + inners.length];
            
            for(int i=0; i<cuInners.length; i++)
                newInners[i] = cuInners[i];
            
            for(int i=0; i<inners.length; i++)
                newInners[cuInners.length+i] = inners[i];
            
            cu.setInners(newInners);
        }
    }


}
