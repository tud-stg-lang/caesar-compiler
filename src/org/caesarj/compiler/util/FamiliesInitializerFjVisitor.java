package org.caesarj.compiler.util;

import org.caesarj.compiler.Compiler;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.ast.FjClassContext;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjInterfaceDeclaration;
import org.caesarj.kjc.CCompilationUnitContext;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JClassImport;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.JInterfaceDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPackageImport;
import org.caesarj.kjc.JPackageName;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcEnvironment;

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
	private Compiler compiler;
	private CContext context;
	
	public 	FamiliesInitializerFjVisitor(Compiler compiler, 
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
		FjClassDeclaration self,
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
