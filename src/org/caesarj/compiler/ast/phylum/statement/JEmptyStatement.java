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
 * $Id: JEmptyStatement.java,v 1.3 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * JLS 14.6: Empty Statement.
 * 
 * An empty statement does nothing.
 */
public class JEmptyStatement extends JStatement {

    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------

    /**
     * Construct a node in the parsing tree
     * 
     * @param where
     *            the line of this node in the source code
     */
    public JEmptyStatement(TokenReference where, JavaStyleComment[] comments) {
        super(where, comments);
    }

    // ----------------------------------------------------------------------
    // SEMANTIC ANALYSIS
    // ----------------------------------------------------------------------

    /**
     * Analyses the statement (semantically).
     * 
     * @param context
     *            the analysis context
     * @exception PositionedError
     *                the analysis detected an error
     */
    public void analyse(CBodyContext context) {
    }

    // ----------------------------------------------------------------------
    // CODE GENERATION
    // ----------------------------------------------------------------------

    /**
     * Generates a sequence of bytescodes
     * 
     * @param code
     *            the code list
     */
    public void genCode(GenerationContext context) {
        // nothing to do here
    }
}