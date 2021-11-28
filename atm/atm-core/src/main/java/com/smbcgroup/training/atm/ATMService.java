package com.smbcgroup.training.atm;

import java.math.BigDecimal;

import com.smbcgroup.training.atm.ATMServiceException.Type;
import com.smbcgroup.training.atm.dao.AccountDAO;
import com.smbcgroup.training.atm.dao.AccountNotFoundException;
import com.smbcgroup.training.atm.dao.UserNotFoundException;

public class ATMService {

	private AccountDAO dao;

	public ATMService(AccountDAO dao) {
		this.dao = dao;
	}

	public User getUser(String userId) throws UserNotFoundException {
		return dao.getUser(userId);
	}

	public Account getAccount(String accountNumber) throws AccountNotFoundException {
		return dao.getAccount(accountNumber);
	}

	public void deposit(String accountNumber, BigDecimal amount) throws AccountNotFoundException, ATMServiceException {
		Account account = getAccount(accountNumber);
		validateIsPositiveAmount(amount);
		account.setBalance(account.getBalance().add(amount));
		dao.updateAccount(account);
	}

	public void withdraw(String accountNumber, BigDecimal amount) throws AccountNotFoundException, ATMServiceException {
		Account account = getAccount(accountNumber);
		validateIsPositiveAmount(amount);
		account.setBalance(account.getBalance().subtract(amount));
		if (!isAboveMinimumBalance(account.getBalance()))
			throw new ATMServiceException(Type.INSUFFICIENT_FUNDS);
		dao.updateAccount(account);
	}

	private void validateIsPositiveAmount(BigDecimal amount) throws ATMServiceException {
		if (BigDecimal.ZERO.compareTo(amount) > 0)
			throw new ATMServiceException(Type.NON_POSITIVE_AMOUNT);
	}

	private boolean isAboveMinimumBalance(BigDecimal balance) {
		return balance.compareTo(BigDecimal.TEN) >= 0;
	}
	
	public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) throws AccountNotFoundException, ATMServiceException{
		
	}

	
	
	
	
	
	
}



