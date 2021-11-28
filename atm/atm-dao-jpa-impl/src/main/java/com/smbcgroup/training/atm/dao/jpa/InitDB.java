package com.smbcgroup.training.atm.dao.jpa;

import java.math.BigDecimal;

import javax.persistence.EntityManager;

public class InitDB {

	public static void main(String[] args) {
		EntityManager em = new AccountJPAImpl().emf.createEntityManager();
		em.getTransaction().begin();

		UserEntity rdelaney = new UserEntity("rdelaney");
		em.persist(rdelaney);
		em.merge(new AccountEntity("123456", new BigDecimal("100"), rdelaney));
		
		UserEntity michael = new UserEntity("michael");
		em.persist(michael);
		em.merge(new AccountEntity("654321", new BigDecimal("56"), michael));

		em.getTransaction().commit();
		em.close();
	}

}
