package com.smbcgroup.training.atm.dao.jpa;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import com.smbcgroup.training.atm.Account;
import com.smbcgroup.training.atm.BankTransaction;
import com.smbcgroup.training.atm.User;
import com.smbcgroup.training.atm.dao.AccountDAO;
import com.smbcgroup.training.atm.dao.AccountNotFoundException;
import com.smbcgroup.training.atm.dao.DuplicateAccountException;
import com.smbcgroup.training.atm.dao.DuplicateUserException;
import com.smbcgroup.training.atm.dao.TransactionNotFoundException;
import com.smbcgroup.training.atm.dao.UserNotFoundException;

public class AccountJPAImpl implements AccountDAO {

	EntityManagerFactory emf = Persistence.createEntityManagerFactory("derby-entities");

	@Override
	public User getUser(String userId) throws UserNotFoundException {
		EntityManager em = emf.createEntityManager();
		try {
			UserEntity entity = em.find(UserEntity.class, userId);
			if (entity == null)
				throw new UserNotFoundException();
			return entity.convertToUser();
		} finally {
			em.close();
		}
	}
	
	@Override
	public BankTransaction getBankTransaction(String date) throws TransactionNotFoundException {
		EntityManager em = emf.createEntityManager();
		try {
			BankTransactionEntity entity = em.find(BankTransactionEntity.class, date);
			if (entity == null) {
				throw new TransactionNotFoundException();
			}
			return entity.convertToBankTransaction();
		}finally {
			em.close();
		}
	}
	
	@Override
	public void logBankTransaction(String accountNumber, String description, BigDecimal amount, BigDecimal postbalance) throws AccountNotFoundException {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			AccountEntity accountEntity = em.find(AccountEntity.class, accountNumber);
			if (accountEntity==null) {
				em.getTransaction().rollback();
				throw new AccountNotFoundException();
			}
			BankTransactionEntity transactionEntity = new BankTransactionEntity();
			Date today = new Date();
			DateFormat detailedDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSS");
			String todayNow = detailedDateFormat.format(today);
			transactionEntity.setAccount(accountEntity);
			transactionEntity.setDate(todayNow);
			transactionEntity.setDescription(description);
			transactionEntity.setAmount(amount);
			transactionEntity.setPostbalance(postbalance);
			em.merge(transactionEntity);
			em.getTransaction().commit();
		}finally {
			em.close();
		}
	}

	@Override
	public Account getAccount(String accountNumber) throws AccountNotFoundException {
		EntityManager em = emf.createEntityManager();
		try {
			AccountEntity entity = em.find(AccountEntity.class, accountNumber);
			if (entity == null)
				throw new AccountNotFoundException();
			return entity.convertToAccount();
		} finally {
			em.close();
		}
	}

	@Override
	public void updateAccount(Account account) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			AccountEntity entity = new AccountEntity();
			entity.setAccountNumber(account.getAccountNumber());
			entity.setBalance(account.getBalance());
			List<BankTransactionEntity> allTransactions = new ArrayList<BankTransactionEntity>();
			for (String transactionDate : account.getTransactions()) {
				allTransactions.add(em.find(BankTransactionEntity.class, transactionDate));
			}
			entity.setTransactions(allTransactions);
			em.merge(entity);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}
	
	@Override
	public Account createNewAccount(String userId) throws UserNotFoundException, DuplicateAccountException{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		String randomAccountNumber = String.format("%06d",(int)(Math.random()*1_000_000));
		try {
			UserEntity userEntity = em.find(UserEntity.class, userId);
			if (userEntity == null) {
				em.getTransaction().rollback();
				throw new UserNotFoundException();
			}
			//randomAccountNumber = "123456";
			AccountEntity accountEntity = new AccountEntity(randomAccountNumber,BigDecimal.ZERO,userEntity);
			if (!(em.find(AccountEntity.class, randomAccountNumber)==null)) {
				em.getTransaction().rollback();
				throw new DuplicateAccountException();
			}
			accountEntity.setTransactions(new ArrayList<BankTransactionEntity>());
			em.merge(accountEntity);
			em.getTransaction().commit();
			return accountEntity.convertToAccount();
		} finally {
			em.close();
		}
	}

	@Override
	public User createNewUser(String userId, boolean needsNewAccount) throws DuplicateAccountException, UserNotFoundException, DuplicateUserException {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			if(em.find(UserEntity.class, userId)!=null) {
				em.getTransaction().rollback();
				throw new DuplicateUserException();
			}
			UserEntity newUserEntity = new UserEntity(userId);
			List<AccountEntity> newList = new ArrayList<AccountEntity>();
			newUserEntity.setAccounts(newList);
			em.persist(newUserEntity);
			if(needsNewAccount) {
				String randomAccountNumber = String.format("%06d",(int)(Math.random()*1_000_000));
				//randomAccountNumber = "123456";
				AccountEntity newAccount = em.find(AccountEntity.class, randomAccountNumber);
				if(newAccount!=null) {
					em.getTransaction().rollback();
					throw new DuplicateAccountException();
				}
				newAccount = new AccountEntity(randomAccountNumber,BigDecimal.ZERO,newUserEntity);
				em.persist(newAccount);
				newList.add(newAccount);
			}
			newUserEntity.setAccounts(newList);
			em.getTransaction().commit();
			return newUserEntity.convertToUser();
		}finally {
			em.close();
		}
	}
	
	public List<BankTransaction> queryTransactionsList(String accountNumber, Date startDate, Date endDate) throws TransactionNotFoundException, AccountNotFoundException {
		EntityManager em = emf.createEntityManager();
		DateFormat detailedDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSS");
		List<BankTransaction> sortedTransactions = new ArrayList<BankTransaction>();
		try{
			AccountEntity accountEntity = em.find(AccountEntity.class, accountNumber);
			if(accountEntity == null) {
				throw new AccountNotFoundException();
			}
			List<BankTransactionEntity> transactionEntities = accountEntity.getTransactions();
			if(startDate!=null && endDate!=null) {
				for(BankTransactionEntity entity : transactionEntities) {
					Date transactionDate = null;
					try {
						transactionDate = detailedDateFormat.parse(entity.getDate());
						if(transactionDate.after(startDate) && transactionDate.before(endDate)) {
							sortedTransactions.add(entity.convertToBankTransaction());
						}
					} catch (Exception e) {
					}
				}
			}else if(startDate==null && endDate!=null) {
				for(BankTransactionEntity entity : transactionEntities) {
					Date transactionDate = null;
					try {
						transactionDate = detailedDateFormat.parse(entity.getDate());
						if(transactionDate.before(endDate)) {
							sortedTransactions.add(entity.convertToBankTransaction());
						}
					} catch (Exception e) {
					}
				}
			}else if(startDate!=null && endDate==null) {
				for(BankTransactionEntity entity : transactionEntities) {
					Date transactionDate = null;
					try {
						transactionDate = detailedDateFormat.parse(entity.getDate());
						if(transactionDate.after(startDate)) {
							sortedTransactions.add(entity.convertToBankTransaction());
						}
					} catch (Exception e) {
					}
				}
			}else {
				for(BankTransactionEntity entity : transactionEntities) {
					sortedTransactions.add(entity.convertToBankTransaction());
				}
			}
			Collections.reverse(sortedTransactions);
			return sortedTransactions;
		}finally {
			em.close();
		}
	}
	
	public List<User> getAllUsers() throws UserNotFoundException {
		EntityManager em = emf.createEntityManager();
		List<UserEntity> queryResults = em.createQuery("SELECT u FROM UserEntity u",UserEntity.class).getResultList();
		List<User> allUsers = new ArrayList<User>();
		for(UserEntity entity : queryResults) {
			allUsers.add(entity.convertToUser());
		}
		return allUsers;
	}
	
	public List<Account> getAllAccounts() throws AccountNotFoundException {
		EntityManager em = emf.createEntityManager();
		List<AccountEntity> queryResults = em.createQuery("SELECT a FROM AccountEntity a",AccountEntity.class).getResultList();
		List<Account> allAccounts = new ArrayList<Account>();
		for(AccountEntity entity : queryResults) {
			allAccounts.add(entity.convertToAccount());
		}
		return allAccounts;
	}

	@Override
	public void deleteAccount(String accountNumber) throws AccountNotFoundException {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try{
			AccountEntity accountForDeletion = em.find(AccountEntity.class, accountNumber);
			if(accountForDeletion==null) {
				em.getTransaction().rollback();
				throw new AccountNotFoundException();
			}
			em.remove(accountForDeletion);
			em.flush();
			em.getTransaction().commit();
		}finally {
			em.close();
		}
	}
	
	@Override
	public void deleteUser(String userId) throws UserNotFoundException {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			UserEntity userForDeletion = em.find(UserEntity.class, userId);
			if(userForDeletion==null) {
				em.getTransaction().rollback();
				throw new UserNotFoundException();
			}
			em.remove(userForDeletion);
			em.flush();
			em.getTransaction().commit();
		}finally {
			em.close();
		}
	}

	@Override
	public void deleteTransaction(String date) throws TransactionNotFoundException {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		try {
			BankTransactionEntity transactionForDeletion = em.find(BankTransactionEntity.class, date);
			if(transactionForDeletion==null) {
				em.getTransaction().rollback();
				throw new TransactionNotFoundException();
			}
			em.remove(transactionForDeletion);
			em.flush();
			em.getTransaction().commit();
		}finally {
			em.close();
		}
		
	}
	
	
	


}
