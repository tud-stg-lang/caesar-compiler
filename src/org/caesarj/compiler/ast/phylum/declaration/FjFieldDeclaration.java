package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.variable.FjVariableDefinition;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.CMember;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.export.FjSourceField;
import org.caesarj.compiler.family.FjFamily;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

public class FjFieldDeclaration extends JFieldDeclaration {

	protected FjFamily family;

	public FjFieldDeclaration(
		TokenReference where,
		JVariableDefinition variable,
		JavadocComment javadoc,
		JavaStyleComment[] comments) {
		super(where, variable, javadoc, comments);
		isChecked = false;
	}

	public FjFieldDeclaration(
		TokenReference where,
		JVariableDefinition variable,
		boolean synthetic,
		JavadocComment javadoc,
		JavaStyleComment[] comments) {
		super(where, variable, synthetic, javadoc, comments);
		isChecked = false;
	}

	public CSourceField checkInterface(CClassContext context)
		throws PositionedError {

		//statically deployed fiels must be final and static
		int modifiers = variable.getModifiers();
		if ((modifiers & ACC_DEPLOYED) != 0
			&& !((modifiers & ACC_FINAL) != 0 && (modifiers & ACC_STATIC) != 0)) {
			context.reportTrouble(
				new PositionedError(
					getTokenReference(),
					CaesarMessages.DEPLOYED_FIELD_NOT_FINAL_AND_STATIC));
		}



		CSourceField field = super.checkInterface(context);



		return field;
	}

	/**
	 * This method initializes the families of this field. It is done 
	 * resolving the families of its variable, then the result of it
	 * is used to create its "source reference". (This reference is set as
	 * interface of it.
	 * 
	 * @param context
	 * @return a new source reference created through the resolved variable.
	 * @author Walter Augusto Werner
	 */
	public CSourceField initFamily(CClassContext context)
	{
		if (context instanceof FjClassContext)
			((FjClassContext)context).pushContextInfo(this);
		
		if (getVariable() instanceof FjVariableDefinition)	
			((FjVariableDefinition)getVariable()).initFamily(context);

		if (context instanceof FjClassContext)
			((FjClassContext)context).popContextInfo();
			
		setInterface(new CSourceField(context.getCClass(),
					  variable.getModifiers(),
					  variable.getIdent(),
					  variable.getType(),
					  isDeprecated(),
					  synthetic)); 
			
		return (CSourceField)getField();
	}
	
	protected void setInterface(CMember export) {
		CField oldField = (CField) export;
		super.setInterface(
			new FjSourceField(
				oldField.getOwner(),
				oldField.getModifiers(),
				oldField.getIdent(),
				oldField.getType(),
				oldField.isDeprecated(),
				oldField.isSynthetic(),
				family));
	}

	private boolean isChecked;
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setFamily(FjFamily family) {
		this.family = family;
	}
}
