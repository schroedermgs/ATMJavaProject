package com.smbcgroup.training.atm.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.smbcgroup.training.atm.Account;
import com.smbcgroup.training.atm.BankTransaction;
import com.smbcgroup.training.atm.User;

public interface AccountDAO {

	User getUser(String userId) throws UserNotFoundException;

	Account getAccount(String accountNumber) throws AccountNotFoundException;

	void updateAccount(Account account);

	BankTransaction getBankTransaction(String date) throws TransactionNotFoundException;

	void logBankTransaction(String accountNumber, String description, BigDecimal amount, BigDecimal postbalance) throws AccountNotFoundException;

	public List<BankTransaction> queryTransactionsList(String accountNumber, Date startDate, Date endDate) throws TransactionNotFoundException, AccountNotFoundException;

	Account createNewAccount(String userId) throws UserNotFoundException, DuplicateAccountException;
	
	User createNewUser(String userId, boolean needsNewAccount) throws DuplicateUserException, DuplicateAccountException, UserNotFoundException;

	List<User> getAllUsers() throws UserNotFoundException;
	
	List<Account> getAllAccounts() throws AccountNotFoundException;
	
	void deleteAccount(String accountNumber) throws AccountNotFoundException;
	
	void deleteTransaction(String date) throws TransactionNotFoundException;
	
	void deleteUser(String userId) throws UserNotFoundException;
}
