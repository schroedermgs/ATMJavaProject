package com.smbcgroup.training.atm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.smbcgroup.training.atm.ATMServiceException.Type;
import com.smbcgroup.training.atm.dao.AccountDAO;
import com.smbcgroup.training.atm.dao.AccountNotFoundException;
import com.smbcgroup.training.atm.dao.DuplicateAccountException;
import com.smbcgroup.training.atm.dao.DuplicateUserException;
import com.smbcgroup.training.atm.dao.TransactionNotFoundException;
import com.smbcgroup.training.atm.dao.UserNotFoundException;

public class ATMServiceTest {

	private MockAccountDAO mockDAO = new MockAccountDAO();
	private ATMService service = new ATMService(mockDAO);

	@Test(expected = UserNotFoundException.class)
	public void testgetUser_AccountNumberDoesntExist() throws Exception {
		mockDAO.stub_getUser(new UserNotFoundException());
		service.getUser("rdelaney");
	}

	@Test
	public void testgetUser_Success() throws Exception {
		User user = new User();
		user.setAccounts(new String[] { "123456" });
		mockDAO.stub_getUser(user);
		assertArrayEquals(new String[] { "123456" }, service.getUser("rdelaney").getAccounts());
	}

	@Test(expected = AccountNotFoundException.class)
	public void testGetBalance_AccountNumberDoesntExist() throws Exception {
		mockDAO.stub_getAccount(new AccountNotFoundException());
		service.getAccount("123456");
	}

	@Test
	public void testGetAccount_Success() throws Exception {
		Account account = new Account();
		account.setAccountNumber("123456");
		account.setBalance(new BigDecimal("100.00"));
		mockDAO.stub_getAccount(account);
		assertSame(account, service.getAccount("123456"));
	}
	
	@Test
	public void test_CheckBalanceMethod() throws Exception{
		Account account = new Account();
		account.setAccountNumber("000111");
		account.setBalance(new BigDecimal("28.74"));
		mockDAO.stub_getAccount(account);
		assertEquals(new BigDecimal("28.74"), service.checkBalance("000111"));
	}

	@Test
	public void testDeposit_AccountNumberDoesntExist() throws Exception {
		mockDAO.stub_getAccount(new AccountNotFoundException());
		try {
			service.deposit("123456", new BigDecimal("99.99"));
			fail();
		} catch (AccountNotFoundException e) {
			assertAccountBalanceNotUpdated();
		}
	}

	@Test
	public void testDeposit_NegativeNumber() throws Exception {
		Account account = new Account();
		account.setAccountNumber("123456");
		account.setBalance(new BigDecimal("100.00"));
		mockDAO.stub_getAccount(account);
		try {
			service.deposit("123456", new BigDecimal("-99.99"));
			fail();
		} catch (ATMServiceException e) {
			assertEquals(Type.NON_POSITIVE_AMOUNT, e.getType());
			assertAccountBalanceNotUpdated();
		}
	}

	@Test
	public void testDeposit_Success() throws Exception {
		Account account = new Account();
		account.setAccountNumber("123456");
		account.setBalance(new BigDecimal("100.00"));
		mockDAO.stub_getAccount(account);
		service.deposit("123456", new BigDecimal("99.99"));
		Account capturedAccount = mockDAO.spy_updateAccount();
		assertEquals("123456", capturedAccount.getAccountNumber());
		assertEquals(new BigDecimal("199.99"), capturedAccount.getBalance());
	}

	@Test
	public void testWithdraw_AccountNumberDoesntExist() throws Exception {
		mockDAO.stub_getAccount(new AccountNotFoundException());
		try {
			service.withdraw("123456", new BigDecimal("99.99"), "people");
			fail();
		} catch (AccountNotFoundException e) {
			assertAccountBalanceNotUpdated();
		}
	}

	@Test
	public void testWithdraw_NegativeNumber() throws Exception {
		Account account = new Account();
		account.setAccountNumber("123456");
		account.setBalance(new BigDecimal("100.00"));
		mockDAO.stub_getAccount(account);
		try {
			service.deposit("123456", new BigDecimal("-99.99"));
			fail();
		} catch (ATMServiceException e) {
			assertEquals(Type.NON_POSITIVE_AMOUNT, e.getType());
			assertAccountBalanceNotUpdated();
		}
	}

	@Test
	public void testWithdraw_InsufficientFunds() throws Exception {
		Account account = new Account();
		account.setAccountNumber("123456");
		account.setBalance(new BigDecimal("100.00"));
		mockDAO.stub_getAccount(account);
		try {
			service.withdraw("123456", new BigDecimal("90.01"), "people");
			fail();
		} catch (ATMServiceException e) {
			assertEquals(Type.INSUFFICIENT_FUNDS, e.getType());
			assertAccountBalanceNotUpdated();
		}
	}

	@Test
	public void testWithdraw_Success() throws Exception {
		Account account = new Account();
		account.setAccountNumber("123456");
		account.setBalance(new BigDecimal("100.00"));
		mockDAO.stub_getAccount(account);
		service.withdraw("123456", new BigDecimal("90"), "people");
		Account capturedAccount = mockDAO.spy_updateAccount();
		assertEquals("123456", capturedAccount.getAccountNumber());
		assertEquals(new BigDecimal("10.00"), capturedAccount.getBalance());
	}
	
	@Test 
	public void testTransfer_Success() throws AccountNotFoundException, ATMServiceException {
		Account fromAccount = new Account();
		fromAccount.setAccountNumber("123456");
		fromAccount.setBalance(new BigDecimal("100.00"));
		Account toAccount = new Account();
		toAccount.setAccountNumber("654321");
		toAccount.setBalance(new BigDecimal("35.00"));
		mockDAO.stub_getAccount(toAccount);
		Account gottenToAccount = mockDAO.getAccount("654321");
		service.transfer("123456", "654321", new BigDecimal("5.00"));
		mockDAO.stub_getAccount(toAccount);
		System.out.println(mockDAO.spy_updateAccount().getBalance());
		//Account capturedAccount = mockDAO.spy_updateAccount();
		//assertEquals("123456",capturedAccount.getAccountNumber());
		//assertEquals(new BigDecimal("95.0"),capturedAccount.getBalance());
	}

	private void assertAccountBalanceNotUpdated() {
		Account capturedAccount = mockDAO.spy_updateAccount();
		assertEquals(null, capturedAccount);
	}

	private static class MockAccountDAO implements AccountDAO {

		private User getUser_value;
		private UserNotFoundException getUser_exception;

		@Override
		public User getUser(String userId) throws UserNotFoundException {
			if (getUser_exception != null)
				throw getUser_exception;
			return getUser_value;
		}

		public void stub_getUser(User user) {
			getUser_value = user;
		}

		public void stub_getUser(UserNotFoundException exception) {
			getUser_exception = exception;
		}

		private Account getAccount_value;
		private AccountNotFoundException getAccount_exception;

		@Override
		public Account getAccount(String accountNumber) throws AccountNotFoundException {
			if (getAccount_exception != null)
				throw getAccount_exception;
			return getAccount_value;
		}

		public void stub_getAccount(Account account) {
			getAccount_value = account;
		}

		public void stub_getAccount(AccountNotFoundException exception) {
			getAccount_exception = exception;
		}

		private Account updateAccount_capture;

		@Override
		public void updateAccount(Account account) {
			updateAccount_capture = account;
		}

		public Account spy_updateAccount() {
			return updateAccount_capture;
		}

		@Override
		public BankTransaction getBankTransaction(String date) throws TransactionNotFoundException {
			return null;
		}

		@Override
		public void logBankTransaction(String accountNumber, String description, BigDecimal amount,
				BigDecimal postbalance) throws AccountNotFoundException {
			
		}

		@Override
		public List<BankTransaction> queryTransactionsList(String accountNumber, Date startDate, Date endDate)
				throws TransactionNotFoundException, AccountNotFoundException {
			return null;
		}

		@Override
		public Account createNewAccount(String userId) throws UserNotFoundException, DuplicateAccountException {
			return null;
		}

		@Override
		public User createNewUser(String userId)
				throws DuplicateUserException, DuplicateAccountException, UserNotFoundException {
			return null;
		}

		@Override
		public List<User> getAllUsers() throws UserNotFoundException {
			return null;
		}

		@Override
		public List<Account> getAllAccounts() throws AccountNotFoundException {
			return null;
		}

		@Override
		public void deleteAccount(String accountNumber) throws AccountNotFoundException {
		}

	}

}
