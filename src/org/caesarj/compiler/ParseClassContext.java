/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: ParseClassContext.java,v 1.4 2004-02-08 16:47:50 ostermann Exp $
 */

package org.caesarj.compiler;

import java.util.ArrayList;
import java.util.Stack;

import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.compiler.ast.JClassBlock;
import org.caesarj.compiler.ast.JFieldDeclaration;
import org.caesarj.compiler.ast.JMethodDeclaration;
import org.caesarj.compiler.ast.JPhylum;
import org.caesarj.compiler.ast.JTypeDeclaration;
import org.caesarj.compiler.ast.PointcutDeclaration;


public class ParseClassContext {

	public static ParseClassContext getInstance() {
		return stack.size() == 0
			? new ParseClassContext()
			: (ParseClassContext) stack.pop();
	}

	public void release() {
		release(this);
	}

	public static void release(ParseClassContext context) {
		context.clear();
		stack.push(context);
	}

	/**
	 * All creations done through getInstance().
	 */
	private ParseClassContext() {
	}

	private void clear() {
		fields.clear();
		methods.clear();
		inners.clear();
		assertions.clear();
		body.clear();
		pointcuts.clear();
		advices.clear();
		declares.clear();
	}

	// ----------------------------------------------------------------------
	// ACCESSORS (ADD)
	// ----------------------------------------------------------------------
	public void addFieldDeclaration(JFieldDeclaration decl) {
		fields.add(decl);
		body.add(decl);
	}

	public void addMethodDeclaration(JMethodDeclaration decl) {
		methods.add(decl);
	}

	public void addAssertionDeclaration(JMethodDeclaration decl) {
		assertions.add(decl);
	}

	public void addInnerDeclaration(JTypeDeclaration decl) {
		inners.add(decl);
	}

	public void addBlockInitializer(JClassBlock block) {
		body.add(block);
	}

	public void addPointcutDeclaration(PointcutDeclaration pointcut) {
		pointcuts.add(pointcut);
	}

	public void addAdviceDeclaration(AdviceDeclaration advice) {
		advices.add(advice);
	}

	public void addDeclare(CaesarDeclare declare) {
		declares.add(declare);
	}

	// ----------------------------------------------------------------------
	// ACCESSORS (GET)
	// ----------------------------------------------------------------------

	public JFieldDeclaration[] getFields() {
		return (JFieldDeclaration[]) fields.toArray(
			new JFieldDeclaration[fields.size()]);
	}

	public JMethodDeclaration[] getMethods() {
		return (JMethodDeclaration[]) methods.toArray(
			new JMethodDeclaration[methods.size()]);
	}

	public JMethodDeclaration[] getAssertions() {
		return (JMethodDeclaration[]) assertions.toArray(
			new JMethodDeclaration[assertions.size()]);
	}

	public JTypeDeclaration[] getInnerClasses() {
		return (JTypeDeclaration[]) inners.toArray(
			new JTypeDeclaration[inners.size()]);
	}

	public JPhylum[] getBody() {
		return (JPhylum[]) body.toArray(new JPhylum[body.size()]);
	}

	public PointcutDeclaration[] getPointcuts() {
		return (PointcutDeclaration[]) pointcuts.toArray(
			new PointcutDeclaration[0]);
	}

	public AdviceDeclaration[] getAdvices() {
		return (AdviceDeclaration[]) advices.toArray(
			new AdviceDeclaration[0]);
	}

	public CaesarDeclare[] getDeclares() {
		return (CaesarDeclare[]) declares.toArray(new CaesarDeclare[0]);
	}

	// ----------------------------------------------------------------------
	// DATA MEMBERS
	// ----------------------------------------------------------------------

	private ArrayList fields = new ArrayList();
	private ArrayList methods = new ArrayList();
	private ArrayList assertions = new ArrayList();
	private ArrayList inners = new ArrayList();
	private ArrayList body = new ArrayList();
	private ArrayList pointcuts = new ArrayList();
	private ArrayList advices = new ArrayList();
	private ArrayList declares = new ArrayList();

	private static Stack stack = new Stack();

}
