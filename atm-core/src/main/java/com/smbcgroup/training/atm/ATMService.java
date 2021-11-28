package com.smbcgroup.training.atm;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.smbcgroup.training.atm.ATMServiceException.Type;
import com.smbcgroup.training.atm.dao.AccountDAO;
import com.smbcgroup.training.atm.dao.AccountNotFoundException;
import com.smbcgroup.training.atm.dao.DuplicateAccountException;
import com.smbcgroup.training.atm.dao.DuplicateUserException;
import com.smbcgroup.training.atm.dao.TransactionNotFoundException;
import com.smbcgroup.training.atm.dao.UserNotFoundException;

public class ATMService {

	private AccountDAO dao;

	public ATMService(AccountDAO dao) {
		this.dao = dao;
	}

	public User getUser(String userId) throws UserNotFoundException {
		return dao.getUser(userId);
	}
	
	public User createNewUser(String userId, boolean needsNewAccount) throws DuplicateAccountException, UserNotFoundException, DuplicateUserException {
		return dao.createNewUser(userId, needsNewAccount);
	}

	public Account getAccount(String accountNumber) throws AccountNotFoundException {
		return dao.getAccount(accountNumber);
	}

	public void deposit(String accountNumber, BigDecimal amount) throws AccountNotFoundException, ATMServiceException {
		Account account = getAccount(accountNumber);
		validateIsPositiveAmount(amount);
		account.setBalance(account.getBalance().add(amount));
		dao.updateAccount(account);
		dao.logBankTransaction(accountNumber, "Online check deposit", amount, account.getBalance());
	}

	public void withdraw(String accountNumber, BigDecimal amount, String payee) throws AccountNotFoundException, ATMServiceException {
		Account account = getAccount(accountNumber);
		validateIsPositiveAmount(amount);
		account.setBalance(account.getBalance().subtract(amount));
		if (!isAboveMinimumBalance(account.getBalance()))
			throw new ATMServiceException(Type.INSUFFICIENT_FUNDS);
		dao.updateAccount(account);
		if (payee==null || payee.length()<2)
			dao.logBankTransaction(accountNumber, "Purchased some goods/ services", BigDecimal.ZERO.subtract(amount), account.getBalance());
		else
			dao.logBankTransaction(accountNumber, "PAID "+payee, BigDecimal.ZERO.subtract(amount), account.getBalance());
	}
	
	public BankTransaction getBankTransaction(String date) throws TransactionNotFoundException {
		return dao.getBankTransaction(date);
	}
	
	public List<BankTransaction> getTransactionsList(String accountNumber, String start, String end) throws TransactionNotFoundException, AccountNotFoundException, ATMServiceException{
		Date startDate = createStartDate(start);
		Date endDate = createEndDate(end);
		if(startDate!=null && endDate!=null && startDate.after(endDate))
			throw new ATMServiceException(Type.INVALID_DATES);
		return dao.queryTransactionsList(accountNumber, startDate, endDate);
	}

	public BigDecimal checkBalance(String accountNumber) throws AccountNotFoundException {
		Account account = getAccount(accountNumber);
		return account.getBalance();
	}
	
	public void transfer(String fromNumber, String toNumber, BigDecimal amount) throws AccountNotFoundException,ATMServiceException{
		if(fromNumber.equals(toNumber))
			throw new ATMServiceException(Type.DUPLICATE_TRANSFER);
		Account fromAccount = getAccount(fromNumber);
		Account toAccount = getAccount(toNumber);
		validateIsPositiveAmount(amount);
		fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
		toAccount.setBalance(toAccount.getBalance().add(amount));
		if (!isAboveMinimumBalance(fromAccount.getBalance()))
			throw new ATMServiceException(Type.INSUFFICIENT_FUNDS);
		dao.updateAccount(fromAccount);
		dao.updateAccount(toAccount);
		dao.logBankTransaction(fromNumber, String.format("User transfer to Acct No. %s",toNumber), BigDecimal.ZERO.subtract(amount), fromAccount.getBalance());
		dao.logBankTransaction(toNumber, String.format("User transfer from Acct No. %s", fromNumber), amount, toAccount.getBalance());
	}
	
	public Account createNewAccount(String userId) throws UserNotFoundException, DuplicateAccountException {
		return dao.createNewAccount(userId);
	}
	
	public List<User> getAllUsers() throws UserNotFoundException{
		return dao.getAllUsers();
	}
	
	public List<Account> getAllAccounts() throws AccountNotFoundException{
		return dao.getAllAccounts();
	}
	
	public void deleteAccount(String accountNumber) throws AccountNotFoundException{
		Account accountForDeletion = dao.getAccount(accountNumber);
		String[] oldTransactions = accountForDeletion.getTransactions();
		for (String oldDate : oldTransactions) {
			try {
				deleteTransaction(oldDate);
			} catch (TransactionNotFoundException e) {
				System.out.println("Could not delete transaction with ID = '" + oldDate + "'.");
			}
		}
		dao.deleteAccount(accountNumber);
	}
	
	public void deleteTransaction(String date) throws TransactionNotFoundException{
		dao.deleteTransaction(date);
	}
	
	public void deleteUser(String userId, boolean areYouSure) throws UserNotFoundException {
		if(areYouSure) {
			User userForDeletion = dao.getUser(userId);
			String[] oldAccounts = userForDeletion.getAccounts();
			for(String oldAccount : oldAccounts) {
				try {
					deleteAccount(oldAccount);
				}catch(AccountNotFoundException e) {
					System.out.println("Could not delete Account #"+oldAccount + ".");
				}
			}
			dao.deleteUser(userId);
		}
	}

	private void validateIsPositiveAmount(BigDecimal amount) throws ATMServiceException {
		if (BigDecimal.ZERO.compareTo(amount) >= 0)
			throw new ATMServiceException(Type.NON_POSITIVE_AMOUNT);
	}

	private boolean isAboveMinimumBalance(BigDecimal balance) {
		return balance.compareTo(BigDecimal.TEN) >= 0;
	}
	
	private Date createStartDate(String start) {
		DateFormat detailedDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSS");
		try {
			String startString = start.replace("/", "-").replace("_", " ")+" 00:00:00:000";
			Date startDate = detailedDateFormat.parse(startString);
			return startDate;
		} catch (ParseException e) {
			return null;
		}
	}
	
	private Date createEndDate(String end) {
		DateFormat detailedDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSS");
		try {
			String endString = end.replace("/", "-").replace("_", " ")+" 23:59:59:999";
			Date endDate = detailedDateFormat.parse(endString);
			return endDate;
		} catch (ParseException e) {
			return null;
		}
	}

}
