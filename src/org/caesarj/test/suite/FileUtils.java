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
 * $Id: FileUtils.java,v 1.2 2005-02-28 13:48:47 aracic Exp $
 */

package org.caesarj.test.suite;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class FileUtils {

    public static void delAllFiles(File dir, String suffix) {
        List files = findAllFiles(dir, suffix);
        for (Iterator it = files.iterator(); it.hasNext();) {
            File file = (File) it.next();
            file.delete();
        }
    }

    public static void delAllFiles(File dir) {
        delAllFiles(dir, "");
    }
    
    public static List findAllFiles(File dir, String pattern) {
        List res = new LinkedList();
        findAllFiles(dir, res, pattern);
        return res;
    }
    
    public static List findAllFiles(File dir) {
        return findAllFiles(dir, "");
    }
    
    private static void findAllFiles(File dir, List lst, String pattern) {
        File[] files = dir.listFiles();
        if (files == null)
            return;

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                findAllFiles(files[i], lst, pattern);
            }
            else if (files[i].getName().matches(pattern)) {
                lst.add(files[i]);
            }
        }
    }
}
