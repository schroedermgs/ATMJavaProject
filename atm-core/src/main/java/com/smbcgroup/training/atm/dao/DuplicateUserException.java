package com.smbcgroup.training.atm.dao;

public class DuplicateUserException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DuplicateUserException() {
		super();
	}
	
	public DuplicateUserException(String message, Throwable cause) {
		super(message, cause);
	}

}