package org.caesarj.compiler.ast.templates;

import org.caesarj.compiler.ast.phylum.declaration.JConstructorDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JAssignmentExpression;
import org.caesarj.compiler.ast.phylum.expression.JCastExpression;
import org.caesarj.compiler.ast.phylum.expression.JConstructorCall;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.statement.JConstructorBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;

/**
 * Generates:
 * 
 * if outerType != null ->
 * 
 * {ident}(Object _$outer) {
 * 		super(_$outer);
 * 		$outer = ({outerType})_$outer;
 * } 
 * 
 * otherwise ->
 * 
 * {ident}(Object _$outer) {
 * 		super(_$outer);
 * }
 * 
 * @author Ivica Aracic 
 */
public class TDefVirtualClassCtorDeclaration extends JConstructorDeclaration {
	
	public TDefVirtualClassCtorDeclaration(
		TokenReference where,
		int modifiers, 
		String ident, 
		CType outerType,
		TypeFactory factory
	) {
		super(
			where, 
			modifiers, 
			ident, 
			new JFormalParameter[]{
				new JFormalParameter(
					where,
					0,
					factory.createReferenceType(TypeFactory.RFT_OBJECT),
					"_$outer",
					true
				)
			}, 
			CReferenceType.EMPTY, 
			new JConstructorBlock(
				where,
				new JConstructorCall(
					where,
					false,
					null,
					new JExpression[] {
						new JNameExpression(where, "_$outer")
					}
				),
				outerType != null ?
					new JStatement[] {
						new JExpressionStatement(
							where,
							new JAssignmentExpression(
								where,
								new JNameExpression(where, "$outer"),
								new JCastExpression(
									where,
									new JNameExpression(where, "_$outer"),
									outerType
								)
							),
							null
						)	
					}
					: JStatement.EMPTY
			), 
			null,
			null,
			factory
		);
	
	}
}
