/*
 * Created on 08.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.caesarj.compiler.cclass;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.CjClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.constants.CaesarConstants;

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

			newTypeDeclarations.add(typeDeclarations[i]);

			if (typeDeclarations[i] instanceof CjClassDeclaration) {

				CjClassDeclaration caesarClass =
					(CjClassDeclaration) typeDeclarations[i];

				CClassFactory utils =
					new CClassFactory(caesarClass, environment);

				// create class interface							
				newTypeDeclarations.add(utils.createCaesarClassInterface());
                
                // rename the class, add implements and rename supertype  
                utils.modifyCaesarClass();


				if (caesarClass.getInners().length > 0) {
					//consider nested types
                    prepareCaesarClass(
						environment, new ClassDeclarationInnerAccessor(caesarClass));
				}
			}
		}
		innerAccessor.setInners(
			(JTypeDeclaration[]) newTypeDeclarations.toArray(
				new JTypeDeclaration[0]));
	}


    /**
     * Offers common access interface for cu and class inners
     *  
     * @author Ivica Aracic
     */
    interface InnerAccessor {
        JTypeDeclaration[] getInners();
        void setInners(JTypeDeclaration[] inners);
    }
    
    
    class ClassDeclarationInnerAccessor implements InnerAccessor {        
        CjClassDeclaration cd;        
        
        ClassDeclarationInnerAccessor(CjClassDeclaration cd) {
            this.cd = cd;
        }
        
		public JTypeDeclaration[] getInners() {
			return cd.getInners();
		}

        public void setInners(JTypeDeclaration[] inners) {
            cd.setInners(inners);
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

        public void setInners(JTypeDeclaration[] inners) {
            cu.setInners(inners);
        }
    }
}
