package org.caesarj.compiler.ast;

import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeContext;
import org.caesarj.kjc.JAssignmentExpression;
import org.caesarj.kjc.JExpressionStatement;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.JVariableDeclarationStatement;
import org.caesarj.kjc.TypeFactory;

public class FjFormalParameter extends JFormalParameter {

	protected FjFamily family;

	public FjFormalParameter(
		TokenReference where,
		int desc,
		CType type,
		String ident,
		boolean isFinal) {
		super(where, desc, type, ident, isFinal);
		cacheDesc = desc;
		typeSwitched = false;
	}
	protected int cacheDesc;
	protected boolean typeSwitched;

	protected Object clone() {
		return new FjFormalParameter(
			getTokenReference(),
			cacheDesc,
			type,
			getIdent(),
			isFinal()
		);
	}

	protected boolean scip() {
		if( name.equals( FjConstants.SELF_NAME ) )
			// self parameter
			// => resolving not nescessary
			return true;

		if( !(type instanceof CReferenceType) )
			// => resolving not nescessary
			return true;
		
		return false;
	}

	public void introduceDownCastVariable( CTypeContext context )
		throws UnpositionedError {
			
		FjMethodDeclaration method = (FjMethodDeclaration)
			((FjAdditionalContext) context).peekContextInfo(1);
		JTypeDeclaration clazz = (JTypeDeclaration)
			((FjAdditionalContext) context).peekContextInfo(2);

		int clazzModifiers = clazz.getModifiers();
		if (((clazzModifiers & FJC_CLEAN) != 0
			|| (clazzModifiers & FJC_VIRTUAL) != 0)
			&& ! (FjConstants.isImplementationMethodName(method.getIdent())
					|| CciConstants.isSettingMethodName(method.getIdent()))) 
		{
			// downcast in clean classes only needed
			// in implementation method or in the method that is sets the 
			//implementation or the binding reference.
			return;
		}

		FjTypeSystem fjts = new FjTypeSystem();
		CReferenceType lowerBound = fjts.lowerBound(
			context,
			getFamily().getType().getCClass(),
			((CReferenceType) type).getIdent() );
			
		method.prependStatement(
			new JExpressionStatement(
				getTokenReference(),
				new JAssignmentExpression(
					getTokenReference(),
					new FjNameExpression(
						getTokenReference(),
						name ),
					new FjCastExpression(
						getTokenReference(),
						new FjNameExpression(
							getTokenReference(),
							FjConstants.renameParameter( name ) ),
						lowerBound ) ),
				null ) );
		method.prependStatement( new JVariableDeclarationStatement( 
			getTokenReference(),
			new FjFamilyVariableDefinition(
				getTokenReference(),
				0,
				lowerBound,
				name,
				null,
				getFamily() ),
			null ) );
		name = FjConstants.renameParameter( name );
	}

	public void upcastOverriddenType( CTypeContext context ) throws UnpositionedError {

		if( scip() )
			return;
			
		if( getFamily() == null )
			return;

		FjTypeSystem fjts = new FjTypeSystem();
		CClass lowerBound = fjts.lowerBound(
			context,
			getFamily().getType().getCClass(),
			getFamily().getInnerType().getIdent() ).getCClass();
		CClass upperBound = fjts.upperBound(
			context,
			getFamily().getType().getCClass(),
			getFamily().getInnerType().getIdent() ).getCClass();

		if( !upperBound.descendsFrom( lowerBound )
		//Walter start
			&& ! context.getClassContext().getCClass().isInterface())
		//Walter end 
		{
			introduceDownCastVariable( context );
		}
	}

	public void addFamily( CTypeContext context ) throws UnpositionedError {

		FjMethodDeclaration method = (FjMethodDeclaration)
			((FjAdditionalContext) context).peekContextInfo(1);

		if( scip() )
			return;
		
		FjTypeSystem fjts = new FjTypeSystem();
		//Walter: inserted the second param in the method call bellow
		FjFamily family = fjts.resolveFamily(context, 
			context.getClassContext().getCClass(), type);
		
		// if the typename is qualified by a variable
		// the qualifier has to be resolved to its type
		if( family != null ) {
			FjFamilyContext.getInstance().setFamilyOf( this, family );
			if( family.isParameter() )
				this.type = family.getInnerType();
			else
				this.type = fjts.upperBound( context, family.getInnerType() );
			this.family = family;
		}
	}
	
	public FjFamily getFamily() {
		return family;
	}
	/* (non-Javadoc)
	 * @see org.caesarj.kjc.JFormalParameter#checkInterface(org.caesarj.kjc.CTypeContext)
	 * Walter
	 */
	public CType checkInterface(CTypeContext context)
	{
		try 
		{
			type = type.checkType(context);
			return type;
		} 
		catch (UnpositionedError cue) 
		{
			 context.reportTrouble(cue.addPosition(getTokenReference()));
			 return context.getTypeFactory().createReferenceType(
			 	TypeFactory.RFT_OBJECT);
		}		
	}

}
