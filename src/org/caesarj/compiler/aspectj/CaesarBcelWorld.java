package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.bcel.BcelWorld;
import org.caesarj.compiler.export.CClass;

/**
 * CaesarBcelWorld.
 * 
 * @author Jürgen Hallpap
 */
public class CaesarBcelWorld extends BcelWorld {

	private static CaesarBcelWorld theInstance;

	public static CaesarBcelWorld getInstance() {
		if (theInstance == null) {
			theInstance = new CaesarBcelWorld();
		}

		return theInstance;
	}

	/**
	 * Constructor for CaesarBcelWorld.
	 */
	private CaesarBcelWorld() {
		super();

	}

	/**
	 * Resolves the given CClass.
	 * 
	 * @param cclass
	 */
	public ResolvedTypeX resolve(CClass cclass) {

		ResolvedTypeX resolvedType =
			(ResolvedTypeX) typeMap.get(
				cclass.getAbstractType().getSignature());
		if (resolvedType == null) {
			ResolvedTypeX.Name name =
				new ResolvedTypeX.Name(
					cclass.getAbstractType().getSignature(),
					this);

			name.setDelegate(new CaesarSourceType(name, false, cclass));

			typeMap.put(cclass.getAbstractType().getSignature(), name);

			resolvedType = name;
		}

		return resolvedType;
	}

}
