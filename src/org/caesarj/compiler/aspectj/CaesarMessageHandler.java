/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: CaesarMessageHandler.java,v 1.7 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.aspectj;

import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.IMessage.Kind;
import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.util.CWarning;
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

			compiler.reportTrouble(
				new CWarning(
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
