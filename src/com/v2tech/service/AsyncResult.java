package com.v2tech.service;

public class AsyncResult {

	Object userObject;
	Object result;
	Exception exception;

	public AsyncResult(Object userObject, Object result) {
		super();
		this.userObject = userObject;
		this.result = result;
	}
	
	public AsyncResult(Object userObject, Object result, Exception exception) {
		super();
		this.userObject = userObject;
		this.result = result;
		this.exception = exception;
	}



	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	
}
