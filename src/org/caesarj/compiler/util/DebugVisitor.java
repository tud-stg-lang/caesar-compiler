package org.caesarj.compiler.util;

import org.caesarj.compiler.ast.FjInterfaceDeclaration;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JClassDeclaration;
import org.caesarj.kjc.JClassImport;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JInterfaceDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPackageImport;
import org.caesarj.kjc.JPackageName;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;


/**
 * @author Walter Augusto Werner
 */
public class DebugVisitor extends FjVisitor
{
	private int ident;
	private String identValue;
	
	/**
	 * 
	 */
	public DebugVisitor()
	{
		this("   ");
	}
	
	public DebugVisitor(String identValue)
	{
		ident = 0;
		this.identValue = identValue; 
	}

	protected void printIdent()
	{
		for (int i = 0; i < ident; i++)
			System.out.print(identValue);
		
	}
	
	protected void addIdent()
	{
		ident++;
	}
	protected void subIdent()
	{
		ident--;
	}
	
	
	/* (non-Javadoc)
	 * @see at.dms.kjc.KjcVisitor#visitClassDeclaration(at.dms.kjc.JClassDeclaration, int, java.lang.String, at.dms.kjc.CTypeVariable[], java.lang.String, at.dms.kjc.CReferenceType[], at.dms.kjc.JPhylum[], at.dms.kjc.JMethodDeclaration[], at.dms.kjc.JTypeDeclaration[])
	 */
	public void visitClassDeclaration(
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
		printIdent();
		self.print();
		printIdent();
		System.out.println("{");
		addIdent();
		for (int i = 0; i < decls.length; i++)
			decls[i].accept(this);
			
		for (int i = 0; i < methods.length; i++)
			methods[i].accept(this);
				
		subIdent();
		printIdent();
		System.out.println("}");
		
	}

	/* (non-Javadoc)
	 * @see at.dms.kjc.KjcVisitor#visitCompilationUnit(at.dms.kjc.JCompilationUnit, at.dms.kjc.JPackageName, at.dms.kjc.JPackageImport[], at.dms.kjc.JClassImport[], at.dms.kjc.JTypeDeclaration[])
	 */
	public void visitCompilationUnit(
		JCompilationUnit self,
		JPackageName packageName,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses,
		JTypeDeclaration[] typeDeclarations)
	{
		for (int i = 0; i < typeDeclarations.length; i++)
			typeDeclarations[i].accept(this);
	}

	/* (non-Javadoc)
	 * @see at.dms.kjc.KjcVisitor#visitInterfaceDeclaration(at.dms.kjc.JInterfaceDeclaration, int, java.lang.String, at.dms.kjc.CReferenceType[], at.dms.kjc.JPhylum[], at.dms.kjc.JMethodDeclaration[])
	 */
	public void visitInterfaceDeclaration(
		JInterfaceDeclaration self,
		int modifiers,
		String ident,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods)
	{
		printIdent();
		self.print();
		printIdent();
		System.out.println("{");
		addIdent();
		JTypeDeclaration[] decls = ((FjInterfaceDeclaration)self).getInners();
	
		for (int i = 0; i < decls.length; i++)
			decls[i].accept(this);

		for (int i = 0; i < methods.length; i++)
			methods[i].accept(this);
			
		subIdent();
		printIdent();
		System.out.println("}");
	}

	/* (non-Javadoc)
	 * @see at.dms.kjc.KjcVisitor#visitMethodDeclaration(at.dms.kjc.JMethodDeclaration, int, at.dms.kjc.CTypeVariable[], at.dms.kjc.CType, java.lang.String, at.dms.kjc.JFormalParameter[], at.dms.kjc.CReferenceType[], at.dms.kjc.JBlock)
	 */
	public void visitMethodDeclaration(
		JMethodDeclaration self,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JBlock body)
	{
		printIdent();
		self.print();
	}
}
