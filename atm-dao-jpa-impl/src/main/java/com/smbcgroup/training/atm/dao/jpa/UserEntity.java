package com.smbcgroup.training.atm.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.smbcgroup.training.atm.User;

@Entity
@Table(name = "Users")
public class UserEntity {

	@Id
	@Column(name = "user_id")
	private String id;

	@OneToMany(mappedBy = "user")
	private List<AccountEntity> accounts;
	

	public UserEntity() {
	}

	public UserEntity(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<AccountEntity> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountEntity> accounts) {
		this.accounts = accounts;
	}
	
	public User convertToUser() {
		User user = new User();
		user.setUserId(id);
		List<String> accountList = new ArrayList<String>();
		for(AccountEntity account : accounts) {
			accountList.add(account.getAccountNumber());
		}
		String[] accountArray = accountList.toArray(new String[accountList.size()]);
		user.setAccounts(accountArray);
		//user.setAccounts(accounts.stream().map(AccountEntity::getAccountNumber).toArray(String[]::new));
		return user;
	}

}
