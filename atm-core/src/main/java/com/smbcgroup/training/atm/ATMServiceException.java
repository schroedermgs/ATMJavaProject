package com.smbcgroup.training.atm;

public class ATMServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	public enum Type {
		NON_POSITIVE_AMOUNT, INSUFFICIENT_FUNDS, DUPLICATE_TRANSFER, INVALID_DATES;
	}
	
	private Type type;
	
	public ATMServiceException(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

}
