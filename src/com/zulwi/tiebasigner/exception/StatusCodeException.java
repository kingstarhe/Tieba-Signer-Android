package com.zulwi.tiebasigner.exception;

@SuppressWarnings("serial")
public class StatusCodeException extends Exception {
	private int code;

	public int getCode() {
		return code;
	}

	public StatusCodeException(String message, int code) {
		super(message);
		this.code = code;
	}
}
