package com.smbcgroup.training.atm.dao.jpa;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;

public class InitDB {

	public static void main(String[] args) {
		EntityManager em = new AccountJPAImpl().emf.createEntityManager();
		em.getTransaction().begin();

		UserEntity rdelaney = new UserEntity("rdelaney");
		em.persist(rdelaney);
		em.merge(new AccountEntity("123456", new BigDecimal("120"), rdelaney));
		
		UserEntity michael = new UserEntity("michael");
		em.persist(michael);
		em.merge(new AccountEntity("654321", new BigDecimal("56"), michael));
		AccountEntity acct111222 = new AccountEntity("111222", new BigDecimal("42"), michael);
		em.persist(acct111222);
		em.merge(new BankTransactionEntity("08-05-2020 16:44:38:532", "Started my first transaction", BigDecimal.ZERO, new BigDecimal("42"), acct111222)); 
		em.merge(new BankTransactionEntity("08-06-2020 09:04:38:532", "Second transaction doing nothing", BigDecimal.ZERO, new BigDecimal("42"), acct111222));
		
		UserEntity bigguy = new UserEntity("bigguy");
		em.persist(bigguy);
		em.merge(new AccountEntity("444555", new BigDecimal("543.25"), bigguy));
		em.merge(new AccountEntity("321321", new BigDecimal("4000"), bigguy));
		em.merge(new AccountEntity("010101", new BigDecimal("51420"), bigguy));
		
		try {
			BankTransactionEntity bankEnt1 = em.find(BankTransactionEntity.class, "08-05-2020 16:44:38:532");
			System.out.println(bankEnt1.getDescription());
		}catch(Exception e) {
			System.out.println("oops!");
		}
		
		em.getTransaction().commit();
		em.close();
	}

}
