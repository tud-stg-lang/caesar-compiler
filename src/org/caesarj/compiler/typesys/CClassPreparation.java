/*
 * Created on 08.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.caesarj.compiler.typesys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
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
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CCompilationUnitContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CCjMixinSourceClass;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CField;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.CVoidType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.java.JavaTypeNode;
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
            if (typeDeclarations[i] instanceof CjVirtualClassDeclaration) {

            	CjVirtualClassDeclaration caesarClass =
                    (CjVirtualClassDeclaration) typeDeclarations[i];

                CClassFactory factory =
                    new CClassFactory(caesarClass, environment);

                // create class interface                           
                JTypeDeclaration cclassIfcDecl = 
                	factory.createCaesarClassInterface();               
        
                // add implements cclass interface and rename supertype to *_Impl  
                factory.modifyCaesarClass();
                
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
    ) throws CaesarTypeSystemException {
	    TypeFactory typeFactory = environment.getTypeFactory();
        ClassReader classReader = environment.getClassReader();
        
        CjInterfaceDeclaration ifcDecl = decl.getMixinIfcDeclaration();
        
        CClass innerClass = inner.getCClass();
                                    
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
        
        CaesarTypeNode topMost = inner.getMixin().getTopmostNode();
        
        CClass returnType = classReader.loadClass(
            typeFactory, 
            topMost.getQualifiedName().toString()    
        );
        
        JMethodDeclaration facMethodDecl = new JMethodDeclaration(
                decl.getTokenReference(),
                ACC_PUBLIC,
                CTypeVariable.EMPTY,
                returnType.getAbstractType(),
                "$new"+inner.getType().getQualifiedName().getIdent(),
                JFormalParameter.EMPTY, // formal params
                CReferenceType.EMPTY,
                creationBlock,
                null, null
            );  
        
        JMethodDeclaration facIfcMethodDecl = new JMethodDeclaration(
            decl.getTokenReference(),
            ACC_PUBLIC,
            CTypeVariable.EMPTY,
            returnType.getAbstractType(),
            "$new"+inner.getType().getQualifiedName().getIdent(),
            JFormalParameter.EMPTY, // formal params
            CReferenceType.EMPTY,
            null,
            null, null
        );  
        
        decl.addMethod(facMethodDecl);
        ifcDecl.addMethod(facIfcMethodDecl);                         
	    
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
        ClassReader classReader = environment.getClassReader();
        
        CClass hashMapClass = 
            classReader.loadClass(
                typeFactory, 
            	"java/util/WeakHashMap"
            );
        
        CjVirtualClassDeclaration innerDecl = inner.getDeclaration();
        CjInterfaceDeclaration innerIfcDecl = innerDecl.getMixinIfcDeclaration();
        
        TokenReference where = decl.getTokenReference();
                
        String wrapperClassIdent = inner.getMixin().getQualifiedName().getIdent();
        String wrapperClassName = inner.getMixin().getQualifiedName().toString().replace('/','.').replace('$','.');
        String wrappeeClassName = innerDecl.getWrappee().getQualifiedName().replace('/','.').replace('$','.');        
        String wrapperMapName = "$"+wrapperClassIdent+"_wrapper_map";
        
        // TODO map = new WeakHashMap() didn't work
        decl.addField(
            new JFieldDeclaration(
                decl.getTokenReference(),
                new JVariableDefinition(
                    where,
                    ACC_PRIVATE,
                    hashMapClass.getAbstractType(),
                    wrapperMapName,
                    null
                ),
                null, null
            )
        );
        
        AstGenerator gen = environment.getAstGenerator();
        
        gen.writeMethod(
            new String[] {
                "public "+wrapperClassName+" "+wrapperClassIdent+"("+wrappeeClassName+" w) {",
                "if (w == null)",
                "    return null;",               
                "if("+wrapperMapName+" == null) "+wrapperMapName+" = new java.util.WeakHashMap();",
                wrapperClassName+" res = ("+wrapperClassName+")"+wrapperMapName+".get(w);",
                "if (res == null) {",
                "    res = this.new "+wrapperClassIdent+"();",
                "    res.$initWrappee(w);",
                "    "+wrapperMapName+".put(w, res);",
                "}",
                "return res;",
                "}"
            }
        );
        
        decl.addMethod(gen.endMethod());
        
        
        gen.writeMethod(
            new String[] {
                "public "+wrapperClassName+" get"+wrapperClassIdent+"("+wrappeeClassName+" w) {",
                "if (w == null)",
                "    return null;",               
                "if("+wrapperMapName+" == null) "+wrapperMapName+" = new java.util.WeakHashMap();",
                wrapperClassName+" res = ("+wrapperClassName+")"+wrapperMapName+".get(w);",
                
                "return res;",
                "}"
            }
        );
        
        decl.addMethod(gen.endMethod());
	}
	
    /**
     * for each inner class which corresponds to a caesar type,
     * we generate for the default ctor of this inner class a factory method in the enclosing class
     * Furthermore, we generate wrapper support for each wrapper in the cclass.
     */
    public void generateSupportMethods(
        CompilerBase compilerBase, 
        KjcEnvironment environment
    ) throws UnpositionedError {
        try {
            CaesarTypeSystem caesarTypeSystem = environment.getCaesarTypeSystem();             
                    
            Collection allTypes = caesarTypeSystem.getJavaTypeGraph().getAllTypes();        
            
            for (Iterator it = allTypes.iterator(); it.hasNext();) {
                JavaTypeNode item = (JavaTypeNode)it.next();                        
                           
                CjVirtualClassDeclaration decl = item.getDeclaration();
                if(decl != null) {                    
                    for (Iterator innerIt = item.getInners().iterator(); innerIt.hasNext();) {
                        JavaTypeNode inner = (JavaTypeNode) innerIt.next();
                        
                        if(!inner.isToBeGeneratedInBytecode()) {
	                        generateFactoryMethod(
	                            environment,
	                            inner,
	                            decl
	                        );
	                        
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
        catch (Exception e) {
            // MSG
        	e.printStackTrace();
        	System.exit(0);
            throw new UnpositionedError(CaesarMessages.FATAL_ERROR);
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
        
        Collection typesToGenerate = caesarTypeSystem.getJavaTypeGraph().getTypesToGenerate();        
        
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
            // mixins are always generated from Caesar source classes
            CCjSourceClass mixinClass = (CCjSourceClass)context.getClassReader().loadClass(
        		context.getTypeFactory(),
        		node.getMixin().getQualifiedImplName().toString()
    		);

            // find owner
            CClass owner = null;
            JavaTypeNode outer = node.getOuter();
            if(outer != null) {                
                owner = generateCClass(outer, context);                
            }
                        
            CCjSourceClass sourceClass = new CCjMixinSourceClass(
                owner,
                TokenReference.NO_REF, // CTODO token reference for generated source classes?
                mixinClass.getModifiers(),
                node.getQualifiedName().getIdent(),
                node.getQualifiedName().toString(),
                CTypeVariable.EMPTY,
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
                generateCClass(itemParent, context).getAbstractType();
            
            superClass = (CReferenceType)superClass.checkType(context);
            
            
            // generate methods
            CMethod mixinMethods[] = mixinClass.getMethods();
            CMethod methods[] = new CMethod[mixinMethods.length+1];
            int i;
            for (i = 0; i < mixinMethods.length; i++) {
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
            
            // def ctor 
            methods[i] = new CSourceMethod(
                    sourceClass,
                    ACC_PROTECTED,
                    JAV_CONSTRUCTOR,
					new CVoidType(),
                    new CType[]{
                		context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT)
            		},
                    CReferenceType.EMPTY,
                    CTypeVariable.EMPTY,
                    false,
                    false,
                    null
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
            
            CClass mixinIfcClass = context.getClassReader().loadClass(
        		context.getTypeFactory(),
        		node.getMixin().getQualifiedName().toString()
			);
            
            // generate interfaces
            CReferenceType interfaces[] = new CReferenceType[]{mixinIfcClass.getAbstractType()};
            
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
