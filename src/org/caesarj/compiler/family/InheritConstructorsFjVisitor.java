package org.caesarj.compiler.family;

import java.util.Vector;

import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjVirtualClassDeclaration;
import org.caesarj.compiler.ast.JCompilationUnit;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.util.PositionedError;
import org.caesarj.util.UnpositionedError;

public class InheritConstructorsFjVisitor
	extends CollectClassesFjVisitor {

	public InheritConstructorsFjVisitor( JCompilationUnit[] compilationUnits ) {		
		super();
		for( int i = 0; i < compilationUnits.length; i++ ) {
			compilationUnits[i].accept( this );
		}
	}

	protected boolean returnClass( FjClassDeclaration decl ) throws PositionedError {
		String superClassKey = null;
		try {
			 superClassKey = FjConstants.toFullQualifiedBaseName(
				decl.getSuperClass().getQualifiedName(), decl.getTypeContext() );
		} catch( UnpositionedError u ) {
			throw u.addPosition( decl.getTokenReference() );
		}
		if( // class inherrits no other virtual class
			decl.getSuperClass().getQualifiedName().equals( FjConstants.CHILD_IMPL_TYPE_NAME ) ||
			// class inherrits a class, but is assumed to already have been changed (is marked)
			markedClasses.get( superClassKey ) != null )
			return true;
		return false;
	}

	protected boolean collectClass( FjClassDeclaration decl ) {
		if( decl instanceof FjVirtualClassDeclaration )
			return true;
		return false;
	}

	public Vector transform() throws PositionedError {
		
		Vector messages = new Vector();		
		if( transformationIsDone )
			return messages;
		
		// all virtual classes have been collected, now
		// inherit all contructors starting from the highest
		// baseclass:
		FjVirtualClassDeclaration nextVirtualClass =
			(FjVirtualClassDeclaration) findNext();
		while( nextVirtualClass != null ) {
			Vector constructorsAppended = 
				nextVirtualClass.inherritConstructorsFromBaseClass( markedClasses );
			if (nextVirtualClass.getWrappee() == null)
			{
				for( int i = 0; i < constructorsAppended.size(); i++ ) {
					UnpositionedError warning =	new UnpositionedError(
						CaesarMessages.MISSING_CONSTRUCTOR,
						constructorsAppended.elementAt( i ).toString(),
						FjConstants.toIfcName( nextVirtualClass.getIdent() ) );
					messages.add( warning.addPosition( nextVirtualClass.getTokenReference() ) );
				}
			}
			nextVirtualClass = (FjVirtualClassDeclaration) findNext();
		}
		
		transformationIsDone = true;
		return messages;
	}
}
