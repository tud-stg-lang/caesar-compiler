package org.caesarj.compiler.aspectj;

import org.aspectj.bridge.IMessageHandler;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.bcel.BcelWorld;
import org.caesarj.compiler.export.CClass;

/**
 * CaesarBcelWorld.
 * 
 * @author Jürgen Hallpap
 */
public class CaesarBcelWorld /*extends BcelWorld */{

	/* the wrapped instance */
	private BcelWorldAdapter	theWorld;

	/* this is the singleton instance */
	private static CaesarBcelWorld theInstance;

	/* returns the singleton instance */
	public static CaesarBcelWorld getInstance() {
		if (theInstance == null) {
			theInstance = new CaesarBcelWorld();
		}

		return theInstance;
	}
	
	/* resets the singleton instance */
	public static void resetInstance() {
		theInstance = null;
	}

	/* returns the BcelWorld object */
	public BcelWorld getWorld(){
		return theWorld;
	}


	/**
	 * Constructor for CaesarBcelWorld.
	 */
	private CaesarBcelWorld() {
		//super();
		theWorld = new BcelWorldAdapter();

	}

	public ResolvedTypeX resolve(CClass cclass){
		return theWorld.resolve(cclass);
	}

	/**
	 *  BcelWorldAdapter allows access to invisible fields of BcelWorld 
	 */
	private class BcelWorldAdapter extends BcelWorld{
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
						theWorld);
	
				name.setDelegate(new CaesarSourceType(name, false, cclass));
	
				typeMap.put(cclass.getAbstractType().getSignature(), name);
	
	
				resolvedType = name;
			}
	
			return resolvedType;
		}
	}

	public void setXnoInline(boolean b) {
		theWorld.setXnoInline(b);
	}

	/**
	 * @param messageHandler
	 */
	public void setMessageHandler(CaesarMessageHandler messageHandler) {
		theWorld.setMessageHandler(messageHandler);
	}

	public IMessageHandler getMessageHandler() {
		return theWorld.getMessageHandler();
	};

}
