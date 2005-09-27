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
 * $Id: CClassPreparation.java,v 1.42 2005-09-27 13:43:53 gasiunas Exp $
 */

package org.caesarj.compiler.cclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.AstGenerator;
import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjInterfaceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.CjVirtualClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.ast.phylum.expression.JUnqualifiedInstanceCreation;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CCompilationUnitContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CCjMixinSourceClass;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CField;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CVoidType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.compiler.typesys.CaesarTypeSystem;
import org.caesarj.compiler.typesys.java.JavaTypeNode;
import org.caesarj.util.TokenReference;
import org.caesarj.util.Utils;

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
            if (typeDeclarations[i] instanceof CjVirtualClassDeclaration) {

            	CjVirtualClassDeclaration caesarClass =
                    (CjVirtualClassDeclaration) typeDeclarations[i];

                CClassFactory factory =
                    new CClassFactory(caesarClass, environment);

                // create class interface                           
                JTypeDeclaration cclassIfcDecl = 
                	factory.createCaesarClassInterface();               
        
                // add implements cclass interface and rename supertype to *_Impl  
                factory.modifyCaesarClass(environment.getTypeFactory());
                
                newTypeDeclarations.add(cclassIfcDecl);

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
         
    
	public void generateFactoryMethod(
        KjcEnvironment environment,
	    JavaTypeNode inner,
	    CjVirtualClassDeclaration decl
    ) {
	    TypeFactory typeFactory = environment.getTypeFactory();
        ClassReader classReader = environment.getClassReader();
        
        CClass innerClass = inner.getCClass();
                                    
        if ((decl.getModifiers() & ACC_ABSTRACT) == 0) {
        	/* create concrete factory method inside concrete collaboration */
        	JBlock creationBlock = new JBlock(
                decl.getTokenReference(),
                new JStatement[] {
                    new JReturnStatement(
                        decl.getTokenReference(),
                        new JUnqualifiedInstanceCreation(
                            decl.getTokenReference(),
                            innerClass.getAbstractType(),
                            //JExpression.EMPTY
    						new JExpression[]{
                        		new JThisExpression(decl.getTokenReference())
                    		}
                        ),
                        null
                    )
                },
                null
            );
        	
        	JMethodDeclaration facMethodDecl = new JMethodDeclaration(
                    decl.getTokenReference(),
                    ACC_PUBLIC,
		            // let the CClassNameType lookup handle this (needed in order to obtain the declaration context)
                    new CClassNameType(inner.getType().getQualifiedName().getIdent()),
                    FACTORY_METHOD_PREFIX+inner.getType().getQualifiedName().getIdent(),
                    JFormalParameter.EMPTY, // formal params
                    CReferenceType.EMPTY,
                    creationBlock,
                    null, null
                );
        	facMethodDecl.setGenerated();
        	decl.addMethod(facMethodDecl);
        }
        else {
        	/* create abstract factory method inside abstract collaboration */
        	JMethodDeclaration facMethodDecl = new JMethodDeclaration(
                    decl.getTokenReference(),
                    ACC_PUBLIC | ACC_ABSTRACT,
					// let the CClassNameType lookup handle this (needed in order to obtain the declaration context)
                    new CClassNameType(inner.getType().getQualifiedName().getIdent()),
                    FACTORY_METHOD_PREFIX+inner.getType().getQualifiedName().getIdent(),
                    JFormalParameter.EMPTY, // formal params
                    CReferenceType.EMPTY,
                    null,
                    null, null
                );
        	facMethodDecl.setGenerated();
        	decl.addMethod(facMethodDecl);
        }
	}
	
	/*
	private WeakHashMap $W_wrapper_map = new WeakHashMap();
	
	private W W(Wrappee $w) {
		W res = (W)$W_wrapper_map.get($w);
		if(res == null) {
			res = $newW();
			res.$initWrappee(wrappee);
			$W_wrapper_map.put($w, res);
		}
		return res;
	}
	*/
	public void generateWrapperSupport(
        KjcEnvironment environment,
	    JavaTypeNode inner,
	    CjVirtualClassDeclaration decl
    ) {
	    TypeFactory typeFactory = environment.getTypeFactory();
        
        CjVirtualClassDeclaration innerDecl = inner.getDeclaration();
        CjInterfaceDeclaration innerIfcDecl = innerDecl.getMixinIfcDeclaration();
        
        TokenReference where = decl.getTokenReference();
                
        String wrapperClassIdent = inner.getMixin().getQualifiedName().getIdent();
        String wrapperClassName = Utils.getClassSourceName(inner.getMixin().getQualifiedName().toString());
        String wrappeeClassName = Utils.getClassSourceName(innerDecl.getWrappee().getQualifiedName());        
        String wrapperMapName = "$"+wrapperClassIdent+"_wrapper_map";
        
        // TODO map = new WeakHashMap() didn't work
        JFieldDeclaration mapDecl = 
        	new JFieldDeclaration(
                decl.getTokenReference(),
                new JVariableDefinition(
                    where,
                    ACC_PRIVATE,
					JLocalVariable.DES_GENERATED,
                    new CClassNameType("java/util/WeakHashMap"),
                    wrapperMapName,
                    null
                ),
				true, // [mef] mark as synthetic
                null, null
            );
        mapDecl.setGenerated();
        decl.addField(mapDecl);
                
        AstGenerator gen = environment.getAstGenerator();
        
        gen.writeMethod(
            new String[] {
                "synchronized public "+wrapperClassIdent+" "+wrapperClassIdent+"("+wrappeeClassName+" w) {",
                "if (w == null)",
                "    return null;",               
                "if("+wrapperMapName+" == null) "+wrapperMapName+" = new java.util.WeakHashMap();",
                wrapperClassIdent+" res = ("+wrapperClassIdent+")"+wrapperMapName+".get(w);",
                "if (res != null) " +
				"    return res;" +
                "res = new "+wrapperClassIdent+"();",
                "res.$initWrappee(w);",
                wrapperMapName+".put(w, res);",
                "return res;",
                "}"
            }
        );
        
        JMethodDeclaration wrapperConstr = gen.endMethod(wrapperClassName+"-"+wrapperClassIdent);
        decl.addMethod( wrapperConstr );        
        
        
        gen.writeMethod(
            new String[] {
                "synchronized public "+wrapperClassIdent+" get"+wrapperClassIdent+"("+wrappeeClassName+" w) {",
                "if (w == null)",
                "    return null;",               
                "if("+wrapperMapName+" == null) "+wrapperMapName+" = new java.util.WeakHashMap();",
                wrapperClassIdent+" res = ("+wrapperClassIdent+")"+wrapperMapName+".get(w);",
                "return res;",
                "}"
            }
        );
        
        JMethodDeclaration wrapperGetter = gen.endMethod("get"+wrapperClassName+"-"+wrapperClassIdent);
        wrapperGetter.setGenerated();
        decl.addMethod(wrapperGetter);
        
        
        /*
        gen.writeMethod(            
            "public "+wrapperClassName+" "+wrapperClassIdent+"("+wrappeeClassName+" w);"
        );
        
        decl.getMixinIfcDeclaration().addMethod(gen.endMethod());
        
        
        gen.writeMethod(
            "public "+wrapperClassName+" get"+wrapperClassIdent+"("+wrappeeClassName+" w);"            
        );
        
        decl.getMixinIfcDeclaration().addMethod(gen.endMethod());
        */
	}
	
    /**
     * for each inner class which corresponds to a caesar type,
     * we generate for the default ctor of this inner class a factory method in the enclosing class
     * Furthermore, we generate wrapper support for each wrapper in the cclass.
     */
    public void generateSupportMethods(
        CompilerBase compilerBase, 
        KjcEnvironment environment
    ) {
        
        CaesarTypeSystem caesarTypeSystem = environment.getCaesarTypeSystem();             
                
        Collection allTypes = caesarTypeSystem.getJavaTypeGraph().getAllTypes();        
        
        for (Iterator it = allTypes.iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode)it.next();                        
                       
            CjVirtualClassDeclaration decl = item.getDeclaration();
            if(decl != null) {                    
                for (Iterator innerIt = item.getInners().iterator(); innerIt.hasNext();) {
                    JavaTypeNode inner = (JavaTypeNode) innerIt.next();
                    
                    if(!inner.isToBeGeneratedInBytecode()) {
                    	
                    	/* generate factory method for non-abstract classes */
                    	if((inner.getDeclaration().getModifiers() & ACC_ABSTRACT) == 0) { 
	                        generateFactoryMethod(
	                            environment,
	                            inner,
	                            decl
	                        );
                    	}
                        
                        if(inner.getDeclaration().isWrapper()) {
	                        generateWrapperSupport(
	                            environment,
	                            inner,
	                            decl
	                        );
                        }
                    }
                }
            }
        }        
    }
    

    /**
     * This step is done after types has been joined
     * 
     * We are generating here missing mixin chain parts
     */
    public void createMixinCloneTypeInfo(
        CompilerBase compilerBase, 
        KjcEnvironment environment,
        JCompilationUnit cu
    ) {
        CaesarTypeSystem caesarTypeSystem = environment.getCaesarTypeSystem(); 
        TypeFactory typeFactory = environment.getTypeFactory();
        ClassReader classReader = environment.getClassReader();
        
        CCompilationUnitContext context =
            new CCompilationUnitContext(compilerBase, environment, cu);
        
        Collection typesToGenerate = caesarTypeSystem.getJavaTypeGraph().getTypesToGenerate();        
        
        for (Iterator it = typesToGenerate.iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
                        
            item.setCClass(
                createMixinCloneCClass(item, context)
            );            
        }
    }
    
    private CClass createMixinCloneCClass(JavaTypeNode node, CContext context) {
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
            // mixins are always generated from Caesar source classes
            CCjSourceClass mixinClass = (CCjSourceClass)context.getClassReader().loadClass(
        		context.getTypeFactory(),
        		node.getMixin().getQualifiedImplName().toString()
    		);

            // find owner
            CClass owner = null;
            JavaTypeNode outer = node.getOuter();
            if(outer != null) {                
                owner = createMixinCloneCClass(outer, context);                
            }
                        
            CCjSourceClass sourceClass = new CCjMixinSourceClass(
                owner,
                TokenReference.NO_REF, // CTODO token reference for generated source classes?
                mixinClass.getModifiers(),
                node.getQualifiedName().getIdent(),
                node.getQualifiedName().toString(),
                false, // deprecated?
                false, // synthetic?
                null, // CTODO: declaration unit is null?
				mixinClass
            );
            
            if(owner != null) {
            	//sourceClass.setHasOuterThis(true);
                sourceClass.setModifiers(sourceClass.getModifiers() | ACC_STATIC);
            }

            
            // generate super type
            JavaTypeNode itemParent = node.getParent();

            CReferenceType superClass = 
                createMixinCloneCClass(itemParent, context).getAbstractType();
            
            superClass = (CReferenceType)superClass.checkType(context);
                       
            CClass mixinIfcClass = context.getClassReader().loadClass(
        		context.getTypeFactory(),
        		node.getMixin().getQualifiedName().toString()
			);
            
            // generate interfaces
            CReferenceType interfaces[] = new CReferenceType[]{mixinIfcClass.getAbstractType()};
            
            sourceClass.setInnerClasses(new CReferenceType[0]);
            
            sourceClass.close(interfaces, superClass, new Hashtable(), CMethod.EMPTY);
                        
            
            // add to class reader
            context.getClassReader().addSourceClass(sourceClass);
            
            return sourceClass;
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    /**
     * This step is done after types has been joined
     * 
     * We are generating here missing mixin chain parts
     */
    public void completeMixinCloneTypeInfo(
        CompilerBase compilerBase, 
        KjcEnvironment environment,
        JCompilationUnit cu
    ) {
        CaesarTypeSystem caesarTypeSystem = environment.getCaesarTypeSystem(); 
        TypeFactory typeFactory = environment.getTypeFactory();
        ClassReader classReader = environment.getClassReader();
        
        CCompilationUnitContext context =
            new CCompilationUnitContext(compilerBase, environment, cu);
        
        Collection typesToGenerate = caesarTypeSystem.getJavaTypeGraph().getTypesToGenerate();        
        
        for (Iterator it = typesToGenerate.iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();
                        
            completeMixinCloneCClass(item, context);        
        }
    }
    
    private void completeMixinCloneCClass(JavaTypeNode node, CContext context) {
        try {
            CCjSourceClass sourceClass = (CCjSourceClass)node.getCClass();
            
            // get mixin export
            // mixins are always generated from Caesar source classes
            CCjSourceClass mixinClass = (CCjSourceClass)context.getClassReader().loadClass(
        		context.getTypeFactory(),
        		node.getMixin().getQualifiedImplName().toString()
    		);


            // generate methods
            // Iterate over JMethodDeclarations and extract formal parameters
            int i;
            JMethodDeclaration mixinMethodsDecl[] = mixinClass.getTypeDeclaration().getMethods();
            CMethod mixinMethod = null;
            List methods = new LinkedList();
            for (i = 0; i < mixinMethodsDecl.length; i++) {
                mixinMethod = mixinMethodsDecl[i].getMethod();
                if(
                	mixinMethod.isConstructor()
                    //|| mixinMethods[i].isCaesarFactoryMethod()
                ) {
                    continue;
                }
                
                methods.add(
                    new CSourceMethod(
	                    sourceClass,
	                    mixinMethod.getModifiers(),
	                    mixinMethod.getIdent(),
	                    mixinMethod.getReturnType(),
	                    mixinMethodsDecl[i].getParameters(),
	                    mixinMethod.getParameters(),
	                    mixinMethod.getThrowables(),
	                    mixinMethod.isDeprecated(),
	                    mixinMethod.isSynthetic(),
	                    null // CTODO JBlock is null?
	                )
                );
            }

            
            // def ctor 
            methods.add(
                new CSourceMethod(
	                sourceClass,
	                ACC_PROTECTED,
	                JAV_CONSTRUCTOR,
					new CVoidType(),
					new JFormalParameter[0], //TODO [mef] : Method parameters for debugging
	                new CType[]{
	            		context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT)
	        		},
	                CReferenceType.EMPTY,
	                false,
	                false,
	                null
	            )
            );
            
            
            // generate fields
            Hashtable fields = new Hashtable();
            CField mixinFields[] = mixinClass.getFields();
            
            for(i=0; i<mixinFields.length; i++) {
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
            
            sourceClass.close(
                sourceClass.getInterfaces(), 
                sourceClass.getSuperType(), 
                fields, 
                (CMethod[])methods.toArray(new CMethod[methods.size()])
            );          
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
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
        CjVirtualClassDeclaration cd;        
        
        ClassDeclarationInnerAccessor(CjVirtualClassDeclaration cd) {
            this.cd = cd;
        }
        
		public JTypeDeclaration[] getInners() {
			return cd.getInners();
		}

        public void addInners(JTypeDeclaration[] inners) {
            cd.getMixinIfcDeclaration().setInners(inners);
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
