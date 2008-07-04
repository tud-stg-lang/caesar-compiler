package org.caesarj.runtime;

public class NotSupportedException extends RuntimeException {
	private static final long serialVersionUID = 5706500180078182248L;
	public final String msg;
	public NotSupportedException(String msg) {
		this.msg = msg;
	}
	public String getMessage() {
		return msg;
	}
}
