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
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * Generates:
 * 
 * if outerType != null ->
 * 
 * {ident}(Object _$outer) { super(_$outer); $outer = ({outerType})_$outer; }
 * 
 * otherwise ->
 * 
 * {ident}(Object _$outer) { super(_$outer); }
 * 
 * @author Ivica Aracic
 */
public class CjVirtualClassCtorDeclaration extends JConstructorDeclaration {

    public CjVirtualClassCtorDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CType outerType,
        TypeFactory factory) {
        super(where, modifiers, ident, new JFormalParameter[] {
            genFormalParameter(where, factory)
        }, CReferenceType.EMPTY, new JConstructorBlock(
            where,
            genSuperCall(where),
            outerType != null ? new JStatement[] {
                genOuterInitializer(where, outerType)
            } : JStatement.EMPTY), null, null, factory);
    }

    public CjVirtualClassCtorDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CType outerType,
        JBlock body,
        TypeFactory factory) {
        super(where, modifiers, ident, new JFormalParameter[] {
            genFormalParameter(where, factory)
        }, CReferenceType.EMPTY, new JConstructorBlock(
            where,
            genSuperCall(where),
            outerType != null ? new JStatement[] {
                    genOuterInitializer(where, outerType), body
            } : new JStatement[] {
                body
            }), null, null, factory);
    }

    public CSourceMethod checkInterface(CClassContext context)
        throws PositionedError {

        // check public
        check(
            context,
            CModifier.contains(getModifiers(), ACC_PUBLIC),
            CaesarMessages.CCLASS_CTOR_PUBLIC);

        // IVICA: check ctor name
        // this check is done in JConstructorDeclaration again
        // here we tune the error message in order to avoid displaying _Impl
        // suffix
        check(
            context,
            ident == context.getCClass().getIdent(),
            KjcMessages.CONSTRUCTOR_BAD_NAME,
            "",
            context.getCClass().getIdent().
            	substring(0, context.getCClass().getIdent().length() - 5)
    	);

        return super.checkInterface(context);
    }

    private static JFormalParameter genFormalParameter(
        TokenReference where,
        TypeFactory factory) {
        return new JFormalParameter(where, 0, factory
            .createReferenceType(TypeFactory.RFT_OBJECT), "_$outer", true);
    }

    private static JConstructorCall genSuperCall(TokenReference where) {
        return new JConstructorCall(where, false, null, new JExpression[] {
            new JNameExpression(where, "_$outer")
        });
    }

    private static JStatement genOuterInitializer(
        TokenReference where,
        CType outerType) {
        return new JExpressionStatement(where, new JAssignmentExpression(
            where,
            new JNameExpression(where, CaesarConstants.OUTER_FIELD),
            new JCastExpression(
                where,
                new JNameExpression(where, "_$outer"),
                outerType)), null);
    }
}