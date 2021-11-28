package com.smbcgroup.training.atm.dao.jpa;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.smbcgroup.training.atm.Account;

@Entity
@Table(name = "Accounts")
public class AccountEntity {

	@Id
	@Column(name = "account_number")
	private String accountNumber;

	@Column(name = "balance")
	private BigDecimal balance;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	public AccountEntity() {

	}

	public AccountEntity(String accountNumber, BigDecimal balance, UserEntity user) {
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.user = user;
	}

	public Account convertToAccount() {
		Account account = new Account();
		account.setAccountNumber(accountNumber);
		account.setBalance(balance);
		return account;
	}

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

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

}
