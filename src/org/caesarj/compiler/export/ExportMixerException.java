package org.caesarj.compiler.export;

/**
 * Will be thrown if mixer algorithm fails
 * 
 * @author Ivica Aracic
 */
public class ExportMixerException extends Exception {
    
	public ExportMixerException() {
		super();
	}

	public ExportMixerException(String message) {
		super(message);
	}

	public ExportMixerException(Throwable cause) {
		super(cause);
	}

	public ExportMixerException(String message, Throwable cause) {
		super(message, cause);
	}

}
