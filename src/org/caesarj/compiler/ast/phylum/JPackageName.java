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
 * $Id: JPackageName.java,v 1.4 2005-03-01 15:38:42 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.util.TokenReference;

/**
 * This class represents the "package org.caesarj.kjc" statement
 */
public class JPackageName extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct a package name
   *
   * @param	where		the token reference of this node
   * @param	name		the package name
   */
  public JPackageName(TokenReference where, String name, JavaStyleComment[] comments, boolean isCollab) {
    super(where);

    this.name = name.intern();
    this.comments = comments;
    this.isCollab = isCollab;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the package name defined by this declaration.
   *
   * @return	the package name defined by this declaration
   */
  public String getName() {
    return name;
  }
  
  /**
   * Returns if the package is a collaboration
   *
   * @return	is it collaboration name
   */
  public boolean isCollaboration() {
    return isCollab;
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  /**
   * The unnamed package (JLS 7.4.2).
   */
  public static final JPackageName	UNNAMED = new JPackageName(TokenReference.NO_REF, "", null, false);

  private final String			name;
  private final JavaStyleComment[]	comments;
  private final boolean			isCollab;
}
