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
 * $Id: CharArrayCache.java,v 1.6 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.util;

import java.util.Stack;

/**
 * This class implements a cache of char arrays
 */
public class CharArrayCache {

  /**
   * Returns a char array.
   */
  public static char[] request() {
    if (stack.empty()) {
      return new char[ARRAY_SIZE];
    } else {
      return (char[])stack.pop();
    }
  }

  /**
   * Releases a char array.
   */
  public static void release(char[] array) {
    stack.push(array);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final int	ARRAY_SIZE = 100000;

  private static Stack		stack = new Stack();
}
