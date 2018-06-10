package com.artyomd.rx;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel {
	@SerializedName("code")
	private Integer code;
	@SerializedName("message")
	private String message;

	@SerializedName("items")
	private List<Response> items;

	public List<Response> getItems() {
		return items;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setItems(List<Response> items) {
		this.items = items;
	}
}