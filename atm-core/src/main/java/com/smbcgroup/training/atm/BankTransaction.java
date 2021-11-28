package com.smbcgroup.training.atm;

import java.math.BigDecimal;
import java.util.Date;

public class BankTransaction {

	private String date;
	private String description;
	private BigDecimal amount;
	private BigDecimal postbalance;
	
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
}
