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
import java.util.List;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CCompilationUnitContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;

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

    /* CLASS PREPARATION BLOCK
     * 
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
            
            if(!isAlreadyGenerated(item, context)) {
                generateCClass(item, context);
            }
        }
    }
    
    public boolean isAlreadyGenerated(JavaTypeNode item, CContext context) {
        return context.getClassReader().hasClassFile(item.getQualifiedName().toString());   
    }
    
    public CClass generateCClass(JavaTypeNode item, CContext context) {
        // CTODO we need binary/source flag for CaesarTypeNode
        // for now consider everything as non-binary
        try {
            // get mixin export
            CReferenceType mixinType = 
                context.getTypeFactory().createType(item.getMixin().getQualifiedImplName().toString(), false);
            
            mixinType = (CReferenceType)mixinType.checkType(context);
            CClass mixinClass = mixinType.getCClass();

            // find owner
            CClass owner = null;
            JavaTypeNode outer = item.getOuter();
            if(outer != null) {
                CReferenceType ownerType = 
                    context.getTypeFactory().createType(outer.getType().getQualifiedImplName().toString(), false);
                
                ownerType = (CReferenceType)ownerType.checkType(context);
                owner = mixinType.getCClass();                
            }
                        
            CSourceClass sourceClass = new CSourceClass(
                owner,
                TokenReference.NO_REF, // CTODO token reference for generated source classes?
                mixinClass.getModifiers(),
                item.getQualifiedName().getIdent(),
                item.getQualifiedName().toString(),
                CTypeVariable.EMPTY,
                false, // deprecated?
                false, // synthetic?
                null // CTODO: declaration unit is null?
            );

            // generate super type
            JavaTypeNode itemParent = item.getParent();
            CReferenceType superClass = null;
            if(itemParent != null) {
                if(
                    itemParent.isToBeGeneratedInBytecode() && 
                    !isAlreadyGenerated(itemParent, context)
                ) {
                    superClass = 
                        generateCClass(itemParent, context).getAbstractType();
                }
                else {
                    superClass = 
                        context.getTypeFactory().createType(
                            itemParent.getQualifiedName().toString(), false
                        );
                }
            }
            else {
                superClass = 
                    context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT);
            }
            
            superClass = (CReferenceType)superClass.checkType(context);
            
            
            // generate methods            
            CMethod methods[] = new CMethod[0]; 
            
            // generate fields
            Hashtable fields = new Hashtable();
            CField mixinFields[] = mixinClass.getFields();
            for(int i=0; i<mixinFields.length; i++) {
                CSourceField clone = new CSourceField(
                    sourceClass,
                    mixinFields[i].getModifiers(),
                    mixinFields[i].getIdent(),
                    mixinFields[i].getType(),
                    false, // deprecated
                    false // synthetic
                );
                
                clone.setPosition(i);
                fields.put(clone.getIdent(), clone);
            }
            
            // generate interfaces
            CReferenceType interfaces[] = new CReferenceType[0];
            
            sourceClass.close(interfaces, superClass, fields, methods);
            
            
            // add to class reader
            context.getClassReader().addSourceClass(sourceClass);
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    
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
