package entities;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class Main {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		EntityManager entityManager = Persistence.createEntityManagerFactory("smart_email").createEntityManager();
		System.out.println(entityManager);
		List<Account> data = entityManager.createQuery("select c from Account c", Account.class).getResultList();
		for(Account d : data) {
			System.out.println(d.getEmail());
		}
	}
}
