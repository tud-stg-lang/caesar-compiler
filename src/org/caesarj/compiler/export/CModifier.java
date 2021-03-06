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
 * $Id: CModifier.java,v 1.8 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.export;

import org.caesarj.compiler.constants.Constants;
import org.caesarj.util.InconsistencyException;

/**
 * This class represents all modifiers token
 */
public class CModifier implements Constants {

    /**
     * generate a list of modifiers
     * @param	modifiers		the modifiers
     */
    public static String toString(int modifiers) {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < CODES.length; i++) {
            if (NAMES[i] != null && (modifiers & CODES[i]) != 0) {
                buffer.append(NAMES[i]);
                buffer.append(" ");
            }
        }
        return buffer.toString();
    }

    // ----------------------------------------------------------------------
    // STATIC UTILITIES
    // ----------------------------------------------------------------------

    /**
     * Tests if a set of modifiers contains a specific flag.
     *
     * @param	modifiers	the set of modifiers
     * @param	flag		the flag to test
     * @return	true iff the set of modifiers contains the flag
     */
    public static boolean contains(int modifiers, int flag) {
        return (modifiers & flag) != 0;
    }

    /**
     * Tests if a set of modifiers contains all specified flags.
     *
     * @param	modifiers	the flags to test
     * @param	mask		the set of modifiers
     * @return	true iff the set of modifiers contains all specified flags
     */
    public static boolean isSubsetOf(int modifiers, int mask) {
        return (modifiers & mask) == modifiers;
    }

    /**
     * Returns the subset of flags contained in a set of modifiers.
     *
     * @param	modifiers	the flags to test
     * @param	mask		the set of modifiers
     * @return	true iff the set of modifiers contains all specified flags
     */
    public static int getSubsetOf(int modifiers, int mask) {
        return modifiers & mask;
    }

    /**
     * Returns the subset flags not contained in a set of modifiers.
     *
     * @param	modifiers	the flags to test
     * @param	mask		the set of modifiers
     * @return	true iff the set of modifiers contains all specified flags
     */
    public static int notElementsOf(int modifiers, int mask) {
        return modifiers & ~mask;
    }

    /**
     * Returns the number of elements of subset of flags contained
     * in a set of modifiers.
     *
     * @param	modifiers	the flags to test
     * @param	mask		the set of modifiers
     * @return	true iff the set of modifiers contains all specified flags
     */
    public static int getSubsetSize(int modifiers, int mask) {
        int count;

        modifiers &= mask;

        count = 0;
        for (int i = 0; i < 32; i++) {
            if (((1 << i) & modifiers) != 0) {
                count += 1;
            }
        }
        return count;
    }

    public static boolean checkOrder(int currentModifiers, int newModifier) {
        return getMaxPosition(currentModifiers) < getPosition(newModifier);
    }

    private static int getMaxPosition(int mod) {
        int max = 0;

        for (int i = 0; i < 32; i++) {
            if (((1 << i) & mod) != 0) {
                max = Math.max(max, getPosition(1 << i));
            }
        }

        return max;
    }

    private static int getPosition(int mod) {
        switch (mod) {
            case ACC_PRIVILEGED :
                return 1;
            case ACC_PUBLIC :
                return 2;
            case ACC_PRIVATE :
                return 3;
            case ACC_PROTECTED :
                return 4;
            case ACC_ABSTRACT :
                return 5;
            case ACC_STATIC :
                return 6;
            case ACC_FINAL :
                return 7;
            case ACC_SYNCHRONIZED :
                return 8;
            case ACC_TRANSIENT :
                return 9;
            case ACC_VOLATILE :
                return 10;
            case ACC_NATIVE :
                return 11;
            case ACC_INTERFACE :
                return 12;
            case ACC_STRICT :
                return 13;
            case ACC_CROSSCUTTING :
                return 14;
            case ACC_DEPLOYED :
                return 15;

            default :
                throw new InconsistencyException();
        }
    }

    /**
     * Returns the name of the specified modifier.
     */
    public static String getName(int mod) {
        switch (mod) {
            case ACC_PUBLIC :
                return "public";
            case ACC_PRIVATE :
                return "private";
            case ACC_PROTECTED :
                return "protected";
            case ACC_STATIC :
                return "static";
            case ACC_FINAL :
                return "final";
            case ACC_SYNCHRONIZED :
                return "synchronized";
            case ACC_VOLATILE :
                return "volatile";
            case ACC_TRANSIENT :
                return "transient";
            case ACC_NATIVE :
                return "native";
            case ACC_INTERFACE :
                return "interface";
            case ACC_ABSTRACT :
                return "abstract";
            case ACC_STRICT :
                return "strictfp";
            case ACC_PRIVILEGED :
                return "privileged";
            case ACC_CROSSCUTTING :
                return "crosscutting";
            case ACC_DEPLOYED :
                return "deploy";
            case ACC_MIXIN_INTERFACE:
                return "mixinifc";
            case ACC_MIXIN:
                return "mixin";
            default :
                throw new InconsistencyException();
        }
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private static final String[] NAMES =
        {
            "public",
            "private",
            "protected",
            "static",
            "final",
            "synchronized",
            "volatile",
            "transient",
            "native",
            null, // interfaces not printed here
            "abstract",
            "strictfp",
            "privileged",
            "crosscutting",
            "deploy",
            "mixinifc",
            "mixin"};

    private static final int[] CODES =
        {
            ACC_PUBLIC,
            ACC_PRIVATE,
            ACC_PROTECTED,
            ACC_STATIC,
            ACC_FINAL,
            ACC_SYNCHRONIZED,
            ACC_VOLATILE,
            ACC_TRANSIENT,
            ACC_NATIVE,
            ACC_INTERFACE,
            ACC_ABSTRACT,
            ACC_STRICT,
            ACC_PRIVILEGED,
            ACC_CROSSCUTTING,
            ACC_DEPLOYED,
            ACC_MIXIN_INTERFACE,
            ACC_MIXIN};
}
