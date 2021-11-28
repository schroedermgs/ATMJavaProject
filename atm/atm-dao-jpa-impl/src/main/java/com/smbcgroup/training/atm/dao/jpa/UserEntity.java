package com.smbcgroup.training.atm.dao.jpa;

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

	public User convertToUser() {
		User user = new User();
		user.setUserId(id);
		user.setAccounts(accounts.stream().map(AccountEntity::getAccountNumber).toArray(String[]::new));
		return user;
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

}
