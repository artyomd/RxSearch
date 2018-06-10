package com.artyomd.rx;

import com.google.gson.annotations.SerializedName;

public class Response {
	@SerializedName("title")
	private String title;

	@SerializedName("link")
	private String url;

	@SerializedName("snippet")
	private String snippet;

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getSnippet() {
		return snippet;
	}
}