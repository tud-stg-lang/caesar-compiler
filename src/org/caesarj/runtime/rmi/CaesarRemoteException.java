/*
 * Created on 16.09.2004
 */
package org.caesarj.runtime.rmi;

/**
 * @author Vaidas Gaiunas
 *
 * Caesar remoting exception
 */
public class CaesarRemoteException extends RuntimeException {

	  /**
	   * Constructs am CaesarRemoteException with no specified detail message.
	   */
	  public CaesarRemoteException() {
	    super();
	  }

	  /**
	   * Constructs am CaesarRemoteException with the specified detail message.
	   *
	   * @param	message		the detail message
	   */
	  public CaesarRemoteException(String message) {
	    super(message);
	  }
}
