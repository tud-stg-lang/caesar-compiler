/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: MethodDescription.java,v 1.1 2003-07-05 18:29:37 werner Exp $
 */
package org.caesarj.classfile;

public class MethodDescription {

  public MethodDescription(String name, String type) {
    this(name, type, null);
  }
  public MethodDescription(String name, String type, String owner) {
    this.name = name;
    this.type = type;
    this.owner = owner;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getOwner() {
    return owner;
  }

  private String name;
  private String type;
  private String owner;
}
