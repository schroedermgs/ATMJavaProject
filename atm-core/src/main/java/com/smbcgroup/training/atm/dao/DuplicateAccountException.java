package com.smbcgroup.training.atm.dao;

public class DuplicateAccountException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DuplicateAccountException() {
		super();
	}
	
	public DuplicateAccountException(String message, Throwable cause) {
		super(message, cause);
	}

}
