package entities;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class Main {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		EntityManager entityManager = Persistence.createEntityManagerFactory("smart_email").createEntityManager();
		System.out.println(entityManager);
		List<Filter> data = entityManager.createQuery("select c from Filter c", Filter.class).getResultList();
		for(Filter d : data) {
			System.out.println(d.getEmail());
		}
	}
}
