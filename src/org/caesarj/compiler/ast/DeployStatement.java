package org.caesarj.compiler.ast;

import org.caesarj.kjc.CBodyContext;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.CSimpleBodyContext;
import org.caesarj.kjc.CodeSequence;
import org.caesarj.kjc.GenerationContext;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JCompoundStatement;
import org.caesarj.kjc.JEmptyStatement;
import org.caesarj.kjc.JEqualityExpression;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JExpressionStatement;
import org.caesarj.kjc.JIfStatement;
import org.caesarj.kjc.JMethodCallExpression;
import org.caesarj.kjc.JNullLiteral;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JTryFinallyStatement;
import org.caesarj.kjc.JTypeNameExpression;
import org.caesarj.kjc.JVariableDeclarationStatement;
import org.caesarj.kjc.TypeFactory;
import org.caesarj.util.Message;
import org.caesarj.compiler.CaesarConstants;
import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

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
public class DeployStatement extends JStatement implements CaesarConstants {

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
	public DeployStatement(
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

		//check type of aspectExpression
		if ((aspectExpression
			.getType(context.getTypeFactory())
			.getCClass()
			.getModifiers()
			& ACC_CROSSCUTTING)
			== 0
			&& !(aspectExpression instanceof JNullLiteral)) {

			context.reportTrouble(
				new PositionedError(
					aspectExpression.getTokenReference(),
					new Message(
						CaesarMessages.DEPLOYED_CLASS_NOT_CROSSCUTTING)));

			return;
		}

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
	 * AspectExpressionType _aspectToDeploy = aspectExpression; 
	 */
	private JStatement createVarDec(CBodyContext context) {
		//consider deploy(null), otherwise compilation error
		if (aspectExpression instanceof JNullLiteral) {
			return new JEmptyStatement(getTokenReference(), null);
		}

		return new JVariableDeclarationStatement(
			getTokenReference(),
			new FjVariableDefinition(
				getTokenReference(),
				0,
				aspectExpression.getType(context.getTypeFactory()),
				deployVariableName,
				aspectExpression),
			null);

	}

	/**
	 * Returns the following statement:
	 *
	 * if (_aspectToDeploy != null)
	 *		_aspectToDeploy.getSingletonAspect()._deploy(_aspectToDeploy, Thread.currentThread());
	 */
	private JStatement createDeployStatement(CBodyContext context) {
		//consider deploy(null), otherwise compilation error
		if (aspectExpression instanceof JNullLiteral) {
			return new JEmptyStatement(getTokenReference(), null);
		}

		JExpression cond =
			new JEqualityExpression(
				getTokenReference(),
				false,
				new FjNameExpression(
					getTokenReference(),
					null,
					deployVariableName),
				new JNullLiteral(getTokenReference()));

		JExpression prefix =
			new JMethodCallExpression(
				getTokenReference(),
				new FjNameExpression(getTokenReference(), deployVariableName),
				GET_SINGLETON_ASPECT_METHOD,
				JExpression.EMPTY);

		JExpression threadPrefix =
			new JTypeNameExpression(
				getTokenReference(),
				new CClassNameType(QUALIFIED_THREAD_CLASS));

		JExpression[] args =
			{
				new FjNameExpression(
					getTokenReference(),
					null,
					deployVariableName),
				new FjMethodCallExpression(
					getTokenReference(),
					threadPrefix,
					"currentThread",
					JExpression.EMPTY)};
		JExpression thenClause =
			new JMethodCallExpression(
				getTokenReference(),
				prefix,
				DEPLOY_METHOD,
				args);
		JStatement ifStatement =
			new JIfStatement(
				getTokenReference(),
				cond,
				new JExpressionStatement(getTokenReference(), thenClause, null),
				new JEmptyStatement(getTokenReference(), null),
				null);
		JStatement[] statements = { ifStatement };
		return new JCompoundStatement(getTokenReference(), statements);

	}

	/**
	 * Returns the following statement:
	 * 
	 * if (_aspectToDeploy != null)
	 *		_aspectToDeploy.getSingletonAspect()._undeploy();
	 */
	private JStatement createUndeployStatement() { //not needed, but should faster
		if (aspectExpression instanceof JNullLiteral) {
			return new JEmptyStatement(getTokenReference(), null);
		}

		JExpression cond =
			new JEqualityExpression(
				getTokenReference(),
				false,
				new FjNameExpression(
					getTokenReference(),
					null,
					deployVariableName),
				new JNullLiteral(getTokenReference()));
		JExpression prefix =
			new JMethodCallExpression(
				getTokenReference(),
				new FjNameExpression(
					getTokenReference(),
					null,
					deployVariableName),
				GET_SINGLETON_ASPECT_METHOD,
				JExpression.EMPTY);
		JExpression thenClause =
			new JMethodCallExpression(
				getTokenReference(),
				prefix,
				UNDEPLOY_METHOD,
				JExpression.EMPTY);

		return new JIfStatement(
			getTokenReference(),
			cond,
			new JExpressionStatement(getTokenReference(), thenClause, null),
			new JEmptyStatement(getTokenReference(), null),
			null);

	}

}
