package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.phylum.expression.JAssignmentExpression;
import org.caesarj.compiler.ast.phylum.expression.JCastExpression;
import org.caesarj.compiler.ast.phylum.expression.JConstructorCall;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
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
public class CjVirtualClassCtorDeclaration extends JConstructorDeclaration {
	
	public CjVirtualClassCtorDeclaration(
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
				new JFormalParameter[]{genFormalParameter(where, factory)}, 
				CReferenceType.EMPTY, 
				new JConstructorBlock(
					where,
					genSuperCall(where),
					outerType != null ?
						new JStatement[] {genOuterInitializer(where, outerType)}
						: JStatement.EMPTY
				), 
				null,
				null,
				factory
			);
	}	
	
	
	public CjVirtualClassCtorDeclaration(
		TokenReference where,
		int modifiers, 
		String ident, 
		CType outerType,
		JBlock body,
		TypeFactory factory
	) {
		super(
			where, 
			modifiers, 
			ident, 
			new JFormalParameter[]{genFormalParameter(where, factory)}, 
			CReferenceType.EMPTY, 
			new JConstructorBlock(
				where,
				genSuperCall(where),
				outerType != null ?
					new JStatement[] {
						genOuterInitializer(where, outerType),
						body
					}
					: new JStatement[]{
						body
					}
			), 
			null,
			null,
			factory
		);
	}	
	
	private static JFormalParameter genFormalParameter(TokenReference where, TypeFactory factory) {
		return 
			new JFormalParameter(
				where,
				0,
				factory.createReferenceType(TypeFactory.RFT_OBJECT),
				"_$outer",
				true
			);
	}
	
	private static JConstructorCall genSuperCall(TokenReference where) {
		return 
			new JConstructorCall(
				where,
				false,
				null,
				new JExpression[] {
					new JNameExpression(where, "_$outer")
				}
			);
	}
	
	private static JStatement genOuterInitializer(TokenReference where, CType outerType) {
		return 
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
			);
	}
}
