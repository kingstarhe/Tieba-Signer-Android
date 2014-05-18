package com.zulwi.tiebasigner.exception;

@SuppressWarnings("serial")
public class ClientApiException extends Exception {
	public final static int NETWORK_FAIL = 1;
	public final static int STATUS_ERROR = 2;
	public final static int PARSE_ERROR = 3;
	public final static int AUTH_FAIL = 4;
	public final static int AUTH_EXPIRED = 5;
	private int code = 0;
	private StatusCodeException httpStatusException;

	public ClientApiException(String message, int code) {
		super(message);
		this.code = code;
	}

	public ClientApiException(int code) {
		this.code = code;
	}

	public ClientApiException(String message) {
		super(message);
	}

	public ClientApiException(StatusCodeException httpStatusException) {
		this.httpStatusException = httpStatusException;
		this.code = STATUS_ERROR;
	}

	public String getMessage() {
		String message = "";
		switch (code) {
			case NETWORK_FAIL:
				message = "网络错误，请检查网络连接！";
				break;
			case STATUS_ERROR:
				message = "HTTP " + httpStatusException.getCode() + "错误！";
				break;
			case PARSE_ERROR:
				message = "JSON解析错误，请检查该站点是否支持客户端！";
				break;
			case AUTH_FAIL:
				message = super.getMessage();
				break;
			case AUTH_EXPIRED:
				message = "登录状态已失效！";
				break;
			default:
				message = super.getMessage();
		}
		return message;
	}

	public StatusCodeException getHttpStatusException() {
		return httpStatusException;
	}

	public int getCode() {
		return code;
	}

}
