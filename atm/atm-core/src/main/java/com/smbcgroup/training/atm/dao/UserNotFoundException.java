package com.smbcgroup.training.atm.dao;

public class UserNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UserNotFoundException() {
		super();
	}
	
	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
