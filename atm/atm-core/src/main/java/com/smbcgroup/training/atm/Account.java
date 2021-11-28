package com.smbcgroup.training.atm;

import java.math.BigDecimal;

public class Account {

	private String accountNumber;
	private BigDecimal balance;

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
