package org.caesarj.compiler.context;

import org.caesarj.compiler.export.CClass;

/**
 * This here is comes near to a hack, but poor
 * extensibility of KOPI gives me no other chance than doing it this way.
 * 
 * We need this class in order to be able to set targets for super and field access 
 * to the current class or super of the current class.
 * 
 * See: CSourceClass.genClassInfo, CField.genStore, CMethod.genCode(TypeFactory, bool) 
 * 
 * @author Ivica Aracic
 */
public class AdditionalGenerationContext {
	
	private CClass currentClass = null;
	
	private static AdditionalGenerationContext singleton = new AdditionalGenerationContext();

	public static AdditionalGenerationContext instance() {
		return singleton;
	}

	private AdditionalGenerationContext() {
	}
	
	
	public CClass getCurrentClass() {
		return currentClass;
	}

	public void setCurrentClass(CClass currentClass) {
		this.currentClass = currentClass;
	}
}
