package entities;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class Main {
	public static void main(String[] args) {
		EntityManager entityManager = Persistence.createEntityManagerFactory("smart_email").createEntityManager();
		System.out.println(entityManager);
		List<Model> data = entityManager.createQuery("select c from Model c", Model.class).getResultList();
		for(Model d : data) {
			System.out.println(d.getEmail());
		}
	}
}
