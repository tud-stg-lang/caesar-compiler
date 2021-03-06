/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: JMemberDeclaration.java,v 1.8 2005-10-12 07:58:17 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CField;
import org.caesarj.compiler.export.CMember;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.TokenReference;


/**
 * This class represents a java class in the syntax tree
 */
public abstract class JMemberDeclaration extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	javadoc		java documentation comments
   * @param	comments	other comments in the source code
   */
  public JMemberDeclaration(TokenReference where,
			    JavadocComment javadoc,
			    JavaStyleComment[] comments)
  {
    super(where);
    this.comments = comments;
    this.javadoc = javadoc;
    generated = false;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (INTERFACE)
  // ----------------------------------------------------------------------

	/**
	 * Returns wether this element is generated by transformations or read 
	 * from source code. 
	 * (If isGenerated() returns true, the element has been generated after 
	 * the parsing step, but the inverse implication is currently not 
	 * necessarily true.) (Karl Klose)
	 */
	public boolean isGenerated(){
		return generated; 
	}
	
	public void setGenerated(){
		generated = true;
	}

  /**
   * Returns true if this member is deprecated
   */
  public boolean isDeprecated() {
    return javadoc != null && javadoc.isDeprecated();
  }
  
  /**
   * Returns true if this member has export information 
   */
  public boolean isExported() {
  	return export != null;
  }

  /**
   * @return	the interface
   */
  public CField getField() {
    return export.getField();
  }

  /**
   * @return	the interface
   */
  public CMethod getMethod() {
    return export.getMethod();
  }

  // ----------------------------------------------------------------------
  // PROTECTED ACCESSORS
  // ----------------------------------------------------------------------

  protected void setInterface(CMember export) {
    this.export = export;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CMember			export;
  protected final JavadocComment		javadoc;
  protected final JavaStyleComment[]	comments;
  
  protected boolean generated;
}
