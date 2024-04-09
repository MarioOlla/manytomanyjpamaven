package it.manytomanyjpamaven.dao;

import jakarta.persistence.EntityManager;

import java.util.List;



public interface IBaseDAO<T> {

	public List<T> list() throws Exception;

	public T get(Long id) throws Exception;

	public void update(T o) throws Exception;

	public void insert(T o) throws Exception;

	public void delete(T o) throws Exception;

	// questo mi serve per l'injection
	public void setEntityManager(EntityManager entityManager);

}
