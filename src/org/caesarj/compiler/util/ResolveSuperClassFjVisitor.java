package org.caesarj.compiler.util;

import java.util.Hashtable;
import java.util.Vector;

import org.caesarj.compiler.Compiler;
import org.caesarj.compiler.Main;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjInterfaceDeclaration;
import org.caesarj.compiler.ast.FjOverrideClassDeclaration;
import org.caesarj.compiler.ast.FjOverrideable;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JClassDeclaration;
import org.caesarj.kjc.JClassImport;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.JInterfaceDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPackageImport;
import org.caesarj.kjc.JPackageName;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;

import sun.misc.Queue;

public class ResolveSuperClassFjVisitor extends FjVisitor {
	
	protected Compiler compiler;
	protected Queue traversationOrder;
	protected Vector compilationUnits;
	protected Hashtable compilationUnitMap;
	
	public ResolveSuperClassFjVisitor( Compiler compiler, Vector compilationUnits ) {
		this.compiler = compiler;
		this.compilationUnits = compilationUnits;
		traversationOrder = new Queue();
		compilationUnitMap = new Hashtable();
		for( int i = 0; i < compilationUnits.size(); i++ ) {
			traversationOrder.enqueue( compilationUnits.elementAt( i ) );
		}
	}

	public void transform() {		
		while( !traversationOrder.isEmpty() ) {
			Object next = null;
			try {
				next = traversationOrder.dequeue();
			} catch( InterruptedException e ) {}
			if( next instanceof JCompilationUnit ) {
				((JCompilationUnit) next).accept( this );
			} else if( next instanceof JTypeDeclaration ) {
				((JTypeDeclaration) next).accept( this );
			}
		}
	}
	
	public void visitCompilationUnit(
		JCompilationUnit self,
		JPackageName packageName,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses,
		JTypeDeclaration[] typeDeclarations) {
		
		for( int i = 0; i < typeDeclarations.length; i++ ) {
			traversationOrder.enqueue( typeDeclarations[ i ] );
			compilationUnitMap.put( typeDeclarations[ i ], self );
		}
	}



	public void visitInterfaceDeclaration(
		JInterfaceDeclaration self,
		int modifiers,
		String ident,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods) {

		if( self instanceof FjOverrideable ) {
			_override( (FjOverrideable) self, compiler );
		}

		JTypeDeclaration[] inners = ((FjInterfaceDeclaration) self).getInners();
		for( int i = 0; i < inners.length; i++ ) {
			enqueue( self, inners[ i ] );
		}
	}

	public void visitClassDeclaration(
		JClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls) {

		if( self instanceof FjOverrideable ) {
			_override( (FjOverrideable) self, compiler );
		}			

		JTypeDeclaration[] inners = ((FjClassDeclaration) self).getInners();
		for( int i = 0; i < inners.length; i++ ) {
			enqueue( self, inners[ i ] );
		}
	}
	
	protected void enqueue( JTypeDeclaration outer, JTypeDeclaration inner ) {
		traversationOrder.enqueue( inner );
		compilationUnitMap.put( inner, compilationUnitMap.get( outer ) );
	}
	
	protected void _override( FjOverrideable instance, Compiler compiler ) {
		FjOverrideClassDeclaration.setOverridingSuperClass( instance, compiler );
		JCompilationUnit unit = (JCompilationUnit) compilationUnitMap.get( instance );
		((Main) compiler).reJoin( unit );
	}
}
