package com.smbcgroup.training.atm.dao.jpa;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import com.smbcgroup.training.atm.BankTransaction;

@Entity
@Table(name = "Transactions")
public class BankTransactionEntity {
	
	@Id
	@Column(name = "date")
	private String date;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "postbalance")
	private BigDecimal postbalance;
	
	@ManyToOne
	@JoinColumn(name = "account_number")
	private AccountEntity account;
	
	
	public BankTransactionEntity() {
	}
	
	public BankTransactionEntity(String date, String description, BigDecimal amount, BigDecimal postbalance, AccountEntity account) {
		this.date = date;
		this.description = description;
		this.amount = amount;
		this.postbalance = postbalance;
		this.account = account;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public AccountEntity getAccount() {
		return account;
	}
	
	public void setAccount(AccountEntity account) {
		this.account = account;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public BigDecimal getPostbalance() {
		return postbalance;
	}
	
	public void setPostbalance(BigDecimal postbalance) {
		this.postbalance = postbalance;
	}
	
	public BankTransaction convertToBankTransaction() {
		BankTransaction transaction = new BankTransaction();
		transaction.setDate(date);
		transaction.setDescription(description);
		transaction.setAmount(amount);
		transaction.setPostbalance(postbalance);
		return transaction;
	}
	
}
