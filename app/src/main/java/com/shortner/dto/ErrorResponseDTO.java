package com.shortner.dto;

public class ErrorResponseDTO {
	
	private String errorCode;
	private String status;
	
	public ErrorResponseDTO() {}

	public ErrorResponseDTO(String errorCode, String status) {
		super();
		this.errorCode = errorCode;
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "UrlErrorResponseDTO [errorCode=" + errorCode + ", status=" + status + "]";
	}

}
