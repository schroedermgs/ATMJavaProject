package com.smbcgroup.training.atm.dao;

public class AccountNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public AccountNotFoundException() {
		super();
	}
	
	public AccountNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
