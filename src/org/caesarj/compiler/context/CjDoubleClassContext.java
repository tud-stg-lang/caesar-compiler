package org.caesarj.compiler.context;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.util.UnpositionedError;

public class CjDoubleClassContext extends FjClassContext {
	
	CClassContext secondContext;

	public CjDoubleClassContext(
			CContext parent,
			KjcEnvironment environment,
			CSourceClass clazz,
			JTypeDeclaration decl,
			CClassContext secondContext) {
		super(parent, environment, clazz, decl);
		this.secondContext = secondContext;
	}
	
	public CClass lookupClass(CClass caller, String name) throws UnpositionedError {
		try {
			return super.lookupClass(caller, name);			
		}
		catch (UnpositionedError e) {
			return secondContext.lookupClass(caller, name);
		}		
	}

}
