/*
 * Created on 20.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.compiler.aspectj;

import java.io.File;

import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelSourceContext;

/**
 * @author vaidas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CaesarBcelSourceContext extends BcelSourceContext {
	
	protected String sourceFileName;
	
	public CaesarBcelSourceContext(BcelObjectType inObject, String fileName) {
		super(inObject);
		sourceFileName = fileName;
	}
		
	public ISourceLocation makeSourceLocation(IHasPosition position) {
		if (position instanceof Advice) {
			return new SourceLocation(getSourceFile(), position.getEnd());
		}
		else {
			return super.makeSourceLocation(position);
		}
	}
	
	protected File getSourceFile() {
		return new File(sourceFileName);
	}
}
