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
 * $Id: MessageDescription.java,v 1.6 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.util;

import java.text.MessageFormat;

/**
 * This class defines message descriptions (errors, warnings, notices, ...)
 * 
 * The message format is a text message with placeholders for its arguments of
 * the form {0}, {1}, ... . Each placeholder will be replaced by the string
 * representation of the corresponding argument.
 */
public class MessageDescription {

    public static final int LVL_UNDEFINED = -1;

    public static final int LVL_ERROR = 0;

    public static final int LVL_CAUTION = 1;

    public static final int LVL_WARNING = 2;

    public static final int LVL_NOTICE = 3;

    public static final int LVL_INFO = 4;

    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------

    /**
     * Constructs a message description
     * 
     * @param format
     *            the textual message format (with placeholders)
     * @param reference
     *            the document describing the reason for this message
     * @param level
     *            the severity level of this message
     */
    public MessageDescription(String format, String reference, int level) {
        this.format = format;
        this.reference = reference;
        this.level = level;
    }

    // ----------------------------------------------------------------------
    // ACCESSORS
    // ----------------------------------------------------------------------

    /**
     * Returns the message format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns a reference to a documentation on this message.
     */
    public String getReference() {
        return reference;
    }

    /**
     * Returns the level of this message.
     */
    public int getLevel() {
        return level;
    }

    // ----------------------------------------------------------------------
    // FORMATTING
    // ----------------------------------------------------------------------

    /**
     * Returns a string explaining the error.
     * 
     * @param parameters
     *            the array of parameters
     */
    public String format(Object[] parameters) {
        String prefix; // the text for the severity level
        String body; // the formatted message
        String suffix; // the reference

        switch (level) {
        case LVL_UNDEFINED:
            // no qualifier
            prefix = "";
            break;
        case LVL_ERROR:
            prefix = "error:";
            break;
        case LVL_CAUTION:
            prefix = "caution:";
            break;
        case LVL_WARNING:
            prefix = "warning:";
            break;
        case LVL_NOTICE:
            prefix = "notice:";
            break;
        case LVL_INFO:
            prefix = "";
            break;
        default:
            // unknown: mark as error
            prefix = "error:";
        }

        try {
            body = MessageFormat.format(format, parameters);
        }
        catch (RuntimeException e) {
            // wrong number of parameters: give at least message text with
            // placeholders
            body = format;
        }

        suffix = reference == null ? "" : " [" + reference + "]";

        return prefix + body + suffix;
    }

    public String toString() {
        return format;
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private final String format;

    private final String reference;

    private int level;
}