package com.smbcgroup.training.atm.dao;

public class TransactionNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TransactionNotFoundException() {
		super();
	}
	
	public TransactionNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
