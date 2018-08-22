package model;

import org.springframework.http.HttpStatus;

public class JsonFormatError {
	private String code;
	private String message;
	public String getCode() {
		return code;
	}
	public void setCode(String badRequest) {
		this.code = badRequest;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
