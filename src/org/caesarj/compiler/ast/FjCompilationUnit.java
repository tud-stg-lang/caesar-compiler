package org.caesarj.compiler.ast;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.joinpoint.DeploymentClassFactory;
import org.caesarj.util.TokenReference;

/**
 * @author andreas
 */
public class FjCompilationUnit extends JCompilationUnit {

	/**
	 * Constructor for FjCompilationUnit.
	 * @param where
	 * @param environment
	 * @param packageName
	 * @param importedPackages
	 * @param importedClasses
	 * @param typeDeclarations
	 */
	public FjCompilationUnit(
		TokenReference where,
		KjcEnvironment environment,
		JPackageName packageName,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses,
		JTypeDeclaration[] typeDeclarations) {
		super(
			where,
			environment,
			packageName,
			importedPackages,
			importedClasses,
			typeDeclarations);
	}

	public void append( JTypeDeclaration decl ) {
		JTypeDeclaration[] newTypeDeclarations = new JTypeDeclaration[ typeDeclarations.length + 1 ];
		for( int i = 0; i < typeDeclarations.length; i++ ) {
			newTypeDeclarations[ i ] = typeDeclarations[ i ];
		}
		newTypeDeclarations[ typeDeclarations.length ] = decl;
		typeDeclarations = newTypeDeclarations;
	}
	
	/**
	 * Replaces the first parameter for the second in the compilation unit.
	 * @param decl
	 * @param newDecl
	 */
	public void replace(JTypeDeclaration decl, JTypeDeclaration newDecl)
	{
		for( int i = 0; i < typeDeclarations.length; i++ ) 
		{
			if (typeDeclarations[i] == decl)
			{
				typeDeclarations[i] = newDecl;
				return;
			}
		}
	}	
	
	public JTypeDeclaration[] getInners() {
		return typeDeclarations;
	}

	public void prepareForDynamicDeployment(KjcEnvironment environment) {	
		List newTypeDeclarations = new ArrayList();
		
		for (int i = 0; i < typeDeclarations.length; i++) {
			
			newTypeDeclarations.add(typeDeclarations[i]);

			if (typeDeclarations[i] instanceof FjClassDeclaration) {

				FjClassDeclaration caesarClass =
					(FjClassDeclaration) typeDeclarations[i];

				if (caesarClass.isCrosscutting() && (!caesarClass.isStaticallyDeployed()) ) {

					DeploymentClassFactory utils =
						new DeploymentClassFactory(
							caesarClass,
							environment);

					//modify the aspect class									
					utils.modifyAspectClass();

					//add the deployment support classes to the enclosing class							
					newTypeDeclarations.add(utils.createAspectInterface());
					newTypeDeclarations.add(utils.createMultiInstanceAspectClass());
					newTypeDeclarations.add(utils.createMultiThreadAspectClass());
					newTypeDeclarations.add(utils.createSingletonAspect());
				}

				if (caesarClass.getInners().length > 0) {
					//consider nested types
					caesarClass.prepareForDynamicDeployment(environment);
				}
			}
		}
		
		typeDeclarations = (JTypeDeclaration[]) newTypeDeclarations.toArray(new JTypeDeclaration[0]);	
	}


}
