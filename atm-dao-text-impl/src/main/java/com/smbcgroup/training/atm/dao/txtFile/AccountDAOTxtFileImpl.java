package com.smbcgroup.training.atm.dao.txtFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import com.smbcgroup.training.atm.Account;
import com.smbcgroup.training.atm.User;
import com.smbcgroup.training.atm.dao.AccountDAO;
import com.smbcgroup.training.atm.dao.AccountNotFoundException;
import com.smbcgroup.training.atm.dao.UserNotFoundException;

public class AccountDAOTxtFileImpl implements AccountDAO {

	static String dataLocation = "data/";

	@Override
	public User getUser(String userId) throws UserNotFoundException {
		try {
			User user = new User();
			user.setUserId(userId);
			user.setAccounts(resourceToString(dataLocation + "users/" + userId).split(","));
			return user;
		} catch (Exception e) {
			System.out.println("IO exception: " + e.getMessage());
			throw new UserNotFoundException("Failed to read accouns for user: " + userId, e);
		}
	}

	@Override
	public Account getAccount(String accountNumber) throws AccountNotFoundException {
		try {
			Account account = new Account();
			account.setAccountNumber(accountNumber);
			account.setBalance(new BigDecimal(resourceToString(dataLocation + "accounts/" + accountNumber)));
			return account;
		} catch (Exception e) {
			throw new AccountNotFoundException("Failed to read balance for account: " + accountNumber, e);
		}
	}

	@Override
	public void updateAccount(Account account) {
		try {
			writeStringToFile(dataLocation + "accounts/" + account.getAccountNumber(),
					account.getBalance().toPlainString());
		} catch (IOException e) {
			throw new RuntimeException("Failed to update balance for account: " + account.getAccountNumber(), e);
		}
	}

	private static String resourceToString(String fileName) throws IOException {
		return Files.readString(Path.of(fileName + ".txt"));
	}

	private static void writeStringToFile(String fileName, String newContents) throws IOException {
		Files.writeString(Path.of(fileName + ".txt"), newContents);
	}

}
