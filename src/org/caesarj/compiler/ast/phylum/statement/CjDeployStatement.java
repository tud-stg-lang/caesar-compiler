package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.phylum.expression.CjMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JTypeNameExpression;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CSimpleBodyContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * The deploy statement is used for dynamic deployment of aspects.
 *  
 * deploy(aspectInstance) {
 * 
 * 	...
 * 
 * }
 * 
 * @author Jürgen Hallpap
 */
public class CjDeployStatement extends JStatement implements CaesarConstants {

	/** 
	 * Static counter for deployStatements,
	 * needed for generation of variable names.
	 */
	private static int counter = 0;

	/** The aspect to deploy dynamically.*/
	private JExpression aspectExpression;

	/** The body of the deploy block.*/
	private JStatement body;

	/** The declaration of a support variable.*/
	private JStatement varDec;

	/** The undeploy()-call needs to be inside a finally block.*/
	private JStatement tryfinallyBlock;

	/** The generated name of the deploy support variable.*/
	private String deployVariableName;

	/**
	 * Constructor for DeployStatement.
	 * 
	 * @param where
	 * @param asepectExpression
	 * @param body
	 * @param comments
	 */
	public CjDeployStatement(
		TokenReference where,
		JExpression aspectExpression,
		JStatement body,
		JavaStyleComment[] comments) {
		super(where, comments);

		this.aspectExpression = aspectExpression;
		this.body = body;

		//We need distinct variable names for the deploy statements in the same method.
		//For simplicity I generate distinct varible names for all deploy statements.
		this.deployVariableName = (ASPECT_TO_DEPLOY + counter).intern();
		counter++;
	}

	/**
	 * Analyses the statement.
	 */
	public void analyse(CBodyContext context) throws PositionedError {

		TypeFactory factory = context.getTypeFactory();

		aspectExpression =
			aspectExpression.analyse(
				new CExpressionContext(context, context.getEnvironment()));


		CBodyContext bodyContext =
			new CSimpleBodyContext(context, context.getEnvironment(), context);

		this.varDec = createVarDec(context);
		this.tryfinallyBlock = createTryFinallyBlock(context);

		varDec.analyse(context);
		tryfinallyBlock.analyse(context);

		//XXX ???
		context.merge(bodyContext);
	}

	public void genCode(GenerationContext context) {
		CodeSequence code = context.getCodeSequence();
		setLineNumber(code);

		varDec.genCode(context);
		tryfinallyBlock.genCode(context);
	}

	/**
	 * Creates the following statement:
	 * 
	 * try {
	 * 		createDeployStatement();
	 *  	body;
	 * } finally {
	 * 		createUndeployStatement
	 * }
	 */
	private JStatement createTryFinallyBlock(CBodyContext context) {
		JStatement[] tryBody = { createDeployStatement(context), body };
		JStatement[] finallyBody = { createUndeployStatement()};

		return new JTryFinallyStatement(
			getTokenReference(),
			new JBlock(getTokenReference(), tryBody, null),
			new JBlock(getTokenReference(), finallyBody, null),
			null);
	}

	/**
	 * Returns the following statement:
	 *
	 * AspectIfc <deployVariableName> = DeploySupport.isDeployable(<DEPLOY_EXPRESSION>); 
	 */
	private JStatement createVarDec(CBodyContext context) {
		//consider deploy(null), otherwise compilation error
		if (aspectExpression instanceof JNullLiteral) {
			return new JEmptyStatement(getTokenReference(), null);
		}

        TokenReference where = getTokenReference();

        JExpression prefix =
            new JTypeNameExpression(
                where,
                new CClassNameType(CAESAR_DEPLOY_SUPPORT_CLASS));

        JExpression checkIfAspectCall =
            new CjMethodCallExpression(
                where,
                prefix,
                "checkIfDeployable",
                new JExpression[] {aspectExpression});

        return new JVariableDeclarationStatement(
			getTokenReference(),
			new JVariableDefinition(
                where,
				0,
				new CClassNameType(CAESAR_ASPECT_IFC),
				deployVariableName,
                checkIfAspectCall),
			null);

	}

	/**
	 * Returns the following statement:
	 *
	 * DeploySupport.deployBlock(<deployVariableName>)
	 */
	private JStatement createDeployStatement(CBodyContext context) {
		//consider deploy(null), otherwise compilation error
		if (aspectExpression instanceof JNullLiteral) {
			return new JEmptyStatement(getTokenReference(), null);
		}
        
        TokenReference where = getTokenReference();

        JExpression prefix =
            new JTypeNameExpression(
                where,
                new CClassNameType(CAESAR_DEPLOY_SUPPORT_CLASS));

        JExpression deployStatementCall =
            new CjMethodCallExpression(
                where,
                prefix,
                "deployBlock",
                new JExpression[] {new JNameExpression(getTokenReference(), deployVariableName)});
        
		return new JExpressionStatement(where, deployStatementCall, null);
	}

	/**
	 * Returns the following statement:
	 * 
	 * DeploySupport.undeployBlock(<deployVariableName>)
	 */
	private JStatement createUndeployStatement() { //not needed, but should faster
		if (aspectExpression instanceof JNullLiteral) {
			return new JEmptyStatement(getTokenReference(), null);
		}

        TokenReference where = getTokenReference();

        JExpression prefix =
            new JTypeNameExpression(
                where,
                new CClassNameType(CAESAR_DEPLOY_SUPPORT_CLASS));
        
        JExpression deployStatementCall =
            new CjMethodCallExpression(
                where,
                prefix,
                "undeployBlock",
                new JExpression[] {new JNameExpression(getTokenReference(), deployVariableName)});
                
        return new JExpressionStatement(where, deployStatementCall, null);

	}

}
