package com.smbcgroup.training.atm.dao.jpa;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import com.smbcgroup.training.atm.Account;
import com.smbcgroup.training.atm.User;
import com.smbcgroup.training.atm.dao.AccountNotFoundException;
import com.smbcgroup.training.atm.dao.DuplicateAccountException;
import com.smbcgroup.training.atm.dao.UserNotFoundException;

public class AccountJPAImplTest {

	private AccountJPAImpl dao = new AccountJPAImpl();

	@Before
	public void setup() {
		EntityManager em = dao.emf.createEntityManager();
		em.getTransaction().begin();

		UserEntity rdelaney = new UserEntity("rdelaney");
		em.persist(rdelaney);
		em.persist(new AccountEntity("123456", new BigDecimal("100"), rdelaney));
		
		UserEntity michael = new UserEntity("michael");
		em.persist(michael);
		em.persist(new AccountEntity("654321", new BigDecimal("56"), michael));
		em.persist(new AccountEntity("111222", new BigDecimal("42"), michael));

		em.getTransaction().commit();
		em.close();
	}

	@Test(expected = UserNotFoundException.class)
	public void testGetUser_UserDoesntExist() throws UserNotFoundException {
		dao.getUser("schan");
	}

	@Test
	public void testGetUser() throws UserNotFoundException {
		User user = dao.getUser("michael");
		assertEquals("michael", user.getUserId());
		assertArrayEquals(new String[] {"654321", "111222"}, user.getAccounts());
	}
	
	@Test
	public void test_AddNewAccountToUser() throws UserNotFoundException, DuplicateAccountException, AccountNotFoundException {
		dao.createNewAccount("michael");
		User user = dao.getUser("michael");
		assertEquals("michael",user.getUserId());
		assertEquals(3,user.getAccounts().length);
		System.out.println(user.getAccounts()[user.getAccounts().length-1]);
		assertEquals(new BigDecimal("0.0"), dao.getAccount(user.getAccounts()[user.getAccounts().length-1]).getBalance() );
	}

	@Test(expected = AccountNotFoundException.class)
	public void testGetAccount_AccountDoesntExist() throws AccountNotFoundException {
		dao.getAccount("123457");
	}

	@Test
	public void testGetAccount() throws AccountNotFoundException {
		Account account = dao.getAccount("123456");
		assertEquals("123456", account.getAccountNumber());
		assertEquals(new BigDecimal("100.0"), account.getBalance());
	}

	@Test
	public void testUpdateAccount_NewAccount() {
		Account newAccount = new Account();
		newAccount.setAccountNumber("123457");
		newAccount.setBalance(new BigDecimal("50"));
		dao.updateAccount(newAccount);
		
		EntityManager em = dao.emf.createEntityManager();
		AccountEntity savedAccount = em.find(AccountEntity.class, "123457");
		assertEquals("123457", savedAccount.getAccountNumber());
		assertEquals(new BigDecimal("50.0"), savedAccount.getBalance());
		em.close();
	}

	@Test
	public void testUpdateAccount_ExistingAccount() {
		Account account = new Account();
		account.setAccountNumber("123456");
		account.setBalance(new BigDecimal("1000"));
		dao.updateAccount(account);
		
		EntityManager em = dao.emf.createEntityManager();
		AccountEntity savedAccount = em.find(AccountEntity.class, "123456");
		assertEquals("123456", savedAccount.getAccountNumber());
		assertEquals(new BigDecimal("1000.0"), savedAccount.getBalance());
		em.close();
	}
	
	@Test
	public void doNothing() {
		
	}

}
