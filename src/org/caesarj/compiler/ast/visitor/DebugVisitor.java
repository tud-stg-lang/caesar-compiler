package org.caesarj.compiler.ast.visitor;

import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.compiler.ast.phylum.JPackageName;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.*;
import org.caesarj.compiler.ast.phylum.expression.*;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JConstructorBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;


/**
 * @author Walter Augusto Werner
 */
public class DebugVisitor extends DeclarationVisitor
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
		
		System.out.println();
		JFieldDeclaration[] fields = self.getFields();
		for (int i = 0; i < fields.length; i++)
			fields[i].accept(this);
			
		System.out.println();

			
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
		JTypeDeclaration[] decls = ((CjInterfaceDeclaration)self).getInners();
	
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
	/* (non-Javadoc)
	 * @see org.caesarj.kjc.KjcVisitor#visitFieldDeclaration(org.caesarj.kjc.JFieldDeclaration, int, org.caesarj.kjc.CType, java.lang.String, org.caesarj.kjc.JExpression)
	 */
	public void visitFieldDeclaration(
		JFieldDeclaration self,
		int modifiers,
		CType type,
		String ident,
		JExpression expr)
	{
		printIdent();
		System.out.print(CModifier.toString(modifiers));
		System.out.print(((CReferenceType)type).getQualifiedName());
		System.out.print(" " + ident + ";");
		//System.out.println(((FjSourceField)self.getField()).getFamily());
	}

	/* (non-Javadoc)
	 * @see org.caesarj.kjc.KjcVisitor#visitConstructorDeclaration(org.caesarj.kjc.JConstructorDeclaration, int, java.lang.String, org.caesarj.kjc.JFormalParameter[], org.caesarj.kjc.CReferenceType[], org.caesarj.kjc.JConstructorBlock)
	 */
	public void visitConstructorDeclaration(
		JConstructorDeclaration self,
		int modifiers,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JConstructorBlock body)
	{
		printIdent();
		self.print();
	}

}
