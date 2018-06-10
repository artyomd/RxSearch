package com.artyomd.rx;

import java.util.List;

/**
 * Created by artyomd on 5/21/17.
 */

public class StreamState {
	private boolean inProgress = false;
	private boolean success = false;
	private String errorMessage;
	private List<Response> data;

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<Response> getData() {
		return data;
	}

	public void setData(List<Response> data) {
		this.data = data;
	}

	public static StreamState createInProgress() {
		StreamState state = new StreamState();
		state.setInProgress(true);
		return state;
	}

	public static StreamState createSuccess(List<Response> data){
		StreamState state = new StreamState();
		state.setSuccess(true);
		state.setData(data);
		return state;
	}

	public static StreamState setError(String errorMessage){
		StreamState state = new StreamState();
		state.setErrorMessage(errorMessage);
		return state;
	}
}
