package org.caesarj.compiler.family;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.JClassDeclaration;
import org.caesarj.compiler.ast.FjInterfaceDeclaration;
import org.caesarj.compiler.ast.FjVisitor;
import org.caesarj.compiler.ast.JClassImport;
import org.caesarj.compiler.ast.JCompilationUnit;
import org.caesarj.compiler.ast.JInterfaceDeclaration;
import org.caesarj.compiler.ast.JMethodDeclaration;
import org.caesarj.compiler.ast.JPackageImport;
import org.caesarj.compiler.ast.JPackageName;
import org.caesarj.compiler.ast.JPhylum;
import org.caesarj.compiler.ast.JTypeDeclaration;
import org.caesarj.compiler.context.CCompilationUnitContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;

/**
 * This visitor runs after the checkInterface is done. The responsability
 * of this visitor is initializate all families. During the checkInterface
 * the fields, parameters and so on that don't have families are resolved, 
 * while the others that do have families are resolved in this round.  
 *  
 * @author Walter Augusto Werner
 */
public class FamiliesInitializerFjVisitor 
	extends FjVisitor
{
	private KjcEnvironment environment;
	private CompilerBase compiler;
	private CContext context;
	
	public 	FamiliesInitializerFjVisitor(CompilerBase compiler, 
		KjcEnvironment environment)
	{
		this.compiler = compiler;
		this.environment = environment;
	}


	public void visitCompilationUnit(
		JCompilationUnit self,
		JPackageName packageName,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses,
		JTypeDeclaration[] typeDeclarations)
	{
		context = new CCompilationUnitContext(compiler, environment, 
			self.getExport());
		super.visitCompilationUnit(self, packageName, importedPackages, 
			importedClasses, typeDeclarations);
	}

	public void visitInterfaceDeclaration(
		JInterfaceDeclaration self,
		int modifiers,
		String ident,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods)
	{
		context = new FjClassContext(context, environment, 
			(CSourceClass)self.getCClass(), self);
		
		((FjClassContext) context).pushContextInfo(self);
		
		if (self instanceof FjInterfaceDeclaration)
			try
			{
				((FjInterfaceDeclaration)self).initFamilies(context.getClassContext());
			}
			catch (PositionedError e)
			{
				context.reportTrouble(e);
			}
		
		((FjClassContext) context).popContextInfo();
		context = context.getParentContext();

	}

	public void visitFjClassDeclaration(
		JClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls)
	{
		context = new FjClassContext(context, environment, 
			(CSourceClass)self.getCClass(), self);
		((FjClassContext) context).pushContextInfo(self);
		
		try
		{
			self.initFamilies(context.getClassContext());
		}
		catch (PositionedError e)
		{
			context.reportTrouble(e);
		}
		super.visitFjClassDeclaration(
			self,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			body,
			methods,
			decls);

		((FjClassContext) context).popContextInfo();
		context = context.getParentContext();
		
	}
}
