package com.smbcgroup.training.atm.dao;

import com.smbcgroup.training.atm.Account;
import com.smbcgroup.training.atm.User;

public interface AccountDAO {

	User getUser(String userId) throws UserNotFoundException;

	Account getAccount(String accountNumber) throws AccountNotFoundException;

	void updateAccount(Account account);

}
