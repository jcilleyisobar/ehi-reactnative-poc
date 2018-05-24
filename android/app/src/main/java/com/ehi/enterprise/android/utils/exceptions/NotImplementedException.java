package com.ehi.enterprise.android.utils.exceptions;

public class NotImplementedException extends UnsupportedOperationException {

	public NotImplementedException() {
		super("One or several required interfaces were not implemented");

	}
}
