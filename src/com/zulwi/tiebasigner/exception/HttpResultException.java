package com.zulwi.tiebasigner.exception;

@SuppressWarnings("serial")
public class HttpResultException extends Exception {
	public final static int NETWORK_FAIL = 1;
	public final static int STATUS_ERROR = NETWORK_FAIL + 1;
	public final static int UNSUPPORTED_ENCODING = STATUS_ERROR + 1;
	public final static int PARSE_ERROR = UNSUPPORTED_ENCODING + 1;
	public final static int AUTH_FAIL = PARSE_ERROR + 1;
	public final static int AUTH_EXPIRED = AUTH_FAIL + 1;
	public final static int UNKNOWN_ERROR = AUTH_EXPIRED + 1;
	private int code = 0;
	private StatusCodeException httpStatusException;

	public HttpResultException(int code) {
		this.code = code;
	}

	public HttpResultException(String message, int code) {
		super(message);
		this.code = code;
	}

	public HttpResultException(StatusCodeException httpStatusException) {
		this.httpStatusException = httpStatusException;
		this.code = STATUS_ERROR;
	}

	public HttpResultException(String message) {
		super(message);
		this.code = UNKNOWN_ERROR;
    }

	@Override
	public String getMessage() {
		String message = "";
		switch (code) {
			case NETWORK_FAIL:
				message = "网络错误，请检查网络连接";
				break;
			case STATUS_ERROR:
				message = "HTTP " + httpStatusException.getCode() + "错误";
				break;
			case UNSUPPORTED_ENCODING:
				message = "编码错误";
				break;
			case PARSE_ERROR:
				message = "JSON解析错误，请检查该站点是否支持客户端";
				break;
			case AUTH_FAIL:
				message = super.getMessage();
				break;
			case AUTH_EXPIRED:
				message = "登录状态已失效";
				break;
			case UNKNOWN_ERROR:
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
