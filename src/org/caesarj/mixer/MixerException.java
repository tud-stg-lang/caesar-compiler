package org.caesarj.mixer;

/**
 * Will be thrown if mixer algorithm fails
 * 
 * @author Ivica Aracic
 */
public class MixerException extends Exception {
    
	public MixerException() {
		super();
	}

	public MixerException(String message) {
		super(message);
	}

	public MixerException(Throwable cause) {
		super(cause);
	}

	public MixerException(String message, Throwable cause) {
		super(message, cause);
	}

}
