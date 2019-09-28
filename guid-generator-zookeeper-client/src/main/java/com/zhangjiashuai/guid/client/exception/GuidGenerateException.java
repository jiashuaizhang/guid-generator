package com.zhangjiashuai.guid.client.exception;

public class GuidGenerateException extends RuntimeException {
	
	private static final long serialVersionUID = 3527815497592733198L;
	
	public GuidGenerateException() {
		super();
	}

	public GuidGenerateException(String message) {
		super(message);
	}

	public GuidGenerateException(String message, Throwable cause) {
		super(message, cause);
	}

	public GuidGenerateException(Throwable cause) {
		super(cause);
	}

}
