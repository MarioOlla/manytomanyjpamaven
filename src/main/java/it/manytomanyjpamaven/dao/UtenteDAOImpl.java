package it.manytomanyjpamaven.dao;

import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public class UtenteDAOImpl implements UtenteDAO {

	private EntityManager entityManager;

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Utente> list() throws Exception {
		// dopo la from bisogna specificare il nome dell'oggetto (lettera maiuscola) e
		// non la tabella
		return entityManager.createQuery("from Utente",Utente.class).getResultList();
	}

	@Override
	public Utente get(Long id) throws Exception {
		return entityManager.find(Utente.class, id);
	}

	@Override
	public void update(Utente utenteInstance) throws Exception {
		if (utenteInstance == null) {
			throw new Exception("Problema valore in input");
		}
		utenteInstance = entityManager.merge(utenteInstance);
	}

	@Override
	public void insert(Utente utenteInstance) throws Exception {
		if (utenteInstance == null) {
			throw new Exception("Problema valore in input");
		}

		entityManager.persist(utenteInstance);
	}

	@Override
	public void delete(Utente utenteInstance) throws Exception {
		if (utenteInstance == null) {
			throw new Exception("Problema valore in input");
		}
		entityManager.remove(entityManager.merge(utenteInstance));
	}

	// questo metodo ci torna utile per capire se possiamo rimuovere un ruolo non
	// essendo collegato ad un utente
	public List<Utente> findAllByRuolo(Ruolo ruoloInput) {
		TypedQuery<Utente> query = entityManager.createQuery("select u FROM Utente u join u.ruoli r where r = :ruolo",Utente.class);
		query.setParameter("ruolo", ruoloInput);
		return query.getResultList();
	}

	@Override
	public Utente findByIdFetchingRuoli(Long id) {
		TypedQuery<Utente> query = entityManager.createQuery("select u FROM Utente u left join fetch u.ruoli r where u.id = :idUtente",Utente.class);
		query.setParameter("idUtente", id);
		return query.getResultList().stream().findFirst().orElse(null);
	}

	@Override
	public List<Utente> allUtentiCreatiNelMeseDi(Date data) throws Exception {
		TypedQuery<Utente> query = entityManager.createQuery("select u FROM Utente u where YEAR(u.dateCreated)= YEAR(:dateCreated) and MONTH(u.dateCreated)= MONTH(:dateCreated)",Utente.class);
		query.setParameter("dateCreated", new java.sql.Date(data.getTime()));
		return query.getResultList();
	}

	@Override
	public List<Utente> allUtentiConPasswordLungaMenoDi8Carattei() throws Exception {
		TypedQuery<Utente> query = entityManager.createQuery("from Utente u where LENGTH(u.password) < 8",Utente.class);
		return query.getResultList();
	}

	@Override
	public boolean anyAdminInUtentiDisabilitati() throws Exception {
		TypedQuery<Utente> query = entityManager.createQuery("from Utente u join fetch u.ruoli r where u.stato = 'DISABILITATO' and r.descrizione='Administrator'",Utente.class);
		return !query.getResultList().isEmpty();
	}

}
