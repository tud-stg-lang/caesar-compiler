package org.caesarj.compiler.aspectj;

import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.IMessage.Kind;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.tools.antlr.extra.CompilerBase;
import org.caesarj.util.Message;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * Handles the AspectJ messages.
 * 
 * @author Jürgen Hallpap
 */
public class CaesarMessageHandler implements IMessageHandler, CaesarConstants {

	/** The compiler handles the message output.*/
	private CompilerBase compiler;

	/**
	 * Constructor for CaesarMessageHandler.
	 */
	public CaesarMessageHandler(CompilerBase compiler) {
		super();

		this.compiler = compiler;
	}

	/**
	 * Handles the (AspectJ) message, by creating a KOPI-error and report it to
	 * the compiler.
	 */
	public boolean handleMessage(IMessage message) throws AbortException {
		if (isIgnoring(message.getKind())) {
			return true;
		}

		ISourceLocation location = message.getISourceLocation();

		if (message.getKind() == IMessage.WARNING) {

			compiler.inform(
				new PositionedError(
					new TokenReference(
						location.getSourceFile().getPath(),
						location.getSourceFile(),
						location.getLine()),
					new Message(
						CaesarMessages.ASPECTJ_WARNING,
						message.getMessage())));
			return true;
		}

		if (location != null) {

			compiler.reportTrouble(
				new PositionedError(
					new TokenReference(
						location.getSourceFile().getPath(),
						location.getSourceFile(),
						location.getLine()),
					new Message(
						CaesarMessages.ASPECTJ_ERROR,
						message.getMessage())));

		} else {

			compiler.reportTrouble(
				new PositionedError(
					TokenReference.NO_REF,
					new Message(
						CaesarMessages.ASPECTJ_ERROR,
						message.getMessage())));

		}

		return true;
	}

	/**
	 * Tells whether the given message kind should be ignored by the handler.
	 */
	public boolean isIgnoring(Kind kind) {
		return kind == IMessage.INFO;
	}

}
