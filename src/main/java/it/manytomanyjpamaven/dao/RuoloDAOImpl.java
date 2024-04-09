package it.manytomanyjpamaven.dao;

import it.manytomanyjpamaven.model.Ruolo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class RuoloDAOImpl implements RuoloDAO {

	private EntityManager entityManager;

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Ruolo> list() throws Exception {
		return entityManager.createQuery("from Ruolo" , Ruolo.class).getResultList();
	}

	@Override
	public Ruolo get(Long id) throws Exception {
		return entityManager.find(Ruolo.class, id);
	}

	@Override
	public void update(Ruolo ruoloInstance) throws Exception {
		if(ruoloInstance == null)
			throw new Exception("Impossibile eseguire oprezaioni sul DB. Input non valido.");
		ruoloInstance = entityManager.merge(ruoloInstance);

	}

	@Override
	public void insert(Ruolo ruoloInstance) throws Exception {
		if (ruoloInstance == null) {
			throw new Exception("Problema valore in input");
		}

		entityManager.persist(ruoloInstance);

	}

	@Override
	public void delete(Ruolo ruoloInstance) throws Exception {
		if(ruoloInstance == null)
			throw new Exception("Impossibile eseguire oprezaioni sul DB. Input non valido.");
		entityManager.remove(entityManager.merge(ruoloInstance));

	}

	@Override
	public Ruolo findByDescrizioneAndCodice(String descrizione, String codice) throws Exception {
		TypedQuery<Ruolo> query = entityManager
				.createQuery("select r from Ruolo r where r.descrizione=?1 and r.codice=?2", Ruolo.class)
				.setParameter(1, descrizione)
				.setParameter(2, codice);
		
		return query.getResultStream().findFirst().orElse(null);
	}

	@Override
	public Long countUtentiAdmin() throws Exception {
		Long result = entityManager.createQuery("select count(u) from Utente u inner join u.ruoli r where r.descrizione='Administrator'", Long.class).getResultStream().findFirst().orElse(null);
		if(result == null)
			throw new Exception("La ricerca non ha prodotto risultati");
		return result;
	}

	@Override
	public List<String> allDescrizioniDistinteRuoliAssociati() throws Exception {
		List<String> result = entityManager.createQuery("select distinct r.descrizione from Utente u inner join u.ruoli r", String.class).getResultList();
		return result;
	}

}
