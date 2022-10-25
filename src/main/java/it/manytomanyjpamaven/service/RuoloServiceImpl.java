package it.manytomanyjpamaven.service;

import java.util.List;

import javax.persistence.EntityManager;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.dao.MyDAOFactory;
import it.manytomanyjpamaven.dao.RuoloDAO;
import it.manytomanyjpamaven.dao.UtenteDAO;
import it.manytomanyjpamaven.exception.UtenteConRuoliAssociatiException;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public class RuoloServiceImpl implements RuoloService {

	private RuoloDAO ruoloDAO;

	@Override
	public List<Ruolo> listAll() throws Exception {
		EntityManager entityManager = EntityManagerUtil.getEntityManager();

		try {

			ruoloDAO.setEntityManager(entityManager);

			return ruoloDAO.list();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EntityManagerUtil.closeEntityManager(entityManager);
		}
	}

	@Override
	public Ruolo caricaSingoloElemento(Long id) throws Exception {
		// questo è come una connection
		EntityManager entityManager = EntityManagerUtil.getEntityManager();

		try {
			// uso l'injection per il dao
			ruoloDAO.setEntityManager(entityManager);

			// eseguo quello che realmente devo fare
			return ruoloDAO.get(id);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EntityManagerUtil.closeEntityManager(entityManager);
		}
	}

	@Override
	public void aggiorna(Ruolo ruoloInstance) throws Exception {
		EntityManager entityManager = EntityManagerUtil.getEntityManager();

		try {
			// questo è come il MyConnection.getConnection()
			entityManager.getTransaction().begin();

			// uso l'injection per il dao
			ruoloDAO.setEntityManager(entityManager);

			// eseguo quello che realmente devo fare
			ruoloDAO.update(ruoloInstance);

			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			throw e;
		} finally {
			EntityManagerUtil.closeEntityManager(entityManager);
		}

	}

	@Override
	public void inserisciNuovo(Ruolo ruoloInstance) throws Exception {
		// questo è come una connection
		EntityManager entityManager = EntityManagerUtil.getEntityManager();

		try {
			// questo è come il MyConnection.getConnection()
			entityManager.getTransaction().begin();

			// uso l'injection per il dao
			ruoloDAO.setEntityManager(entityManager);

			// eseguo quello che realmente devo fare
			ruoloDAO.insert(ruoloInstance);

			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			throw e;
		} finally {
			EntityManagerUtil.closeEntityManager(entityManager);
		}

	}

	@Override
	public void rimuovi(Long idRuoloToRemove) throws Exception {
		EntityManager entityManager = EntityManagerUtil.getEntityManager();

		UtenteDAO utenteDaoInstance = MyDAOFactory.getUtenteDAOInstance();

		try {
			// questo è come il MyConnection.getConnection()
			entityManager.getTransaction().begin();

			// uso l'injection per il dao
			ruoloDAO.setEntityManager(entityManager);
			utenteDaoInstance.setEntityManager(entityManager);

			List<Utente> tuttiGliUtentiConRuolo = utenteDaoInstance.findAllByRuolo(ruoloDAO.get(idRuoloToRemove));

			for (Utente utente : tuttiGliUtentiConRuolo) {
				utenteDaoInstance.findByIdFetchingRuoli(utente.getId());
				utente.getRuoli().remove(ruoloDAO.get(idRuoloToRemove));
				utenteDaoInstance.update(utente);
			}

			ruoloDAO.delete(ruoloDAO.get(idRuoloToRemove));

			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			throw e;
		} finally {
			EntityManagerUtil.closeEntityManager(entityManager);
		}

	}

	@Override
	public void setRuoloDAO(RuoloDAO ruoloDAO) {
		this.ruoloDAO = ruoloDAO;
	}

	@Override
	public Ruolo cercaPerDescrizioneECodice(String descrizione, String codice) throws Exception {
		// questo è come una connection
		EntityManager entityManager = EntityManagerUtil.getEntityManager();

		try {
			// uso l'injection per il dao
			ruoloDAO.setEntityManager(entityManager);

			// eseguo quello che realmente devo fare
			return ruoloDAO.findByDescrizioneAndCodice(descrizione, codice);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EntityManagerUtil.closeEntityManager(entityManager);
		}
	}

	@Override
	public void rimuoviSoloSeNessunUtente(Long idRuoloToRemove) throws Exception {
		EntityManager entityManager = EntityManagerUtil.getEntityManager();
		UtenteDAO utenteDaoInstance = MyDAOFactory.getUtenteDAOInstance();

		try {
			// questo è come il MyConnection.getConnection()
			entityManager.getTransaction().begin();
			
			utenteDaoInstance.setEntityManager(entityManager);
			ruoloDAO.setEntityManager(entityManager);
			
			if(!utenteDaoInstance.findAllByRuolo(ruoloDAO.get(idRuoloToRemove)).isEmpty())
				throw new UtenteConRuoliAssociatiException("Errore durante la cancellazione. Ci sono ancora utenti con questo ruolo.");

			ruoloDAO.delete(ruoloDAO.get(idRuoloToRemove));

			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			throw e;
		} finally {
			EntityManagerUtil.closeEntityManager(entityManager);
		}

	}

	@Override
	public Long quantiUtentiAdmin() throws Exception {
		EntityManager entityManager = EntityManagerUtil.getEntityManager();

		try {

			ruoloDAO.setEntityManager(entityManager);

			return ruoloDAO.countUtentiAdmin();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EntityManagerUtil.closeEntityManager(entityManager);
		}
	}

	@Override
	public List<String> tutteLeDescrizioniDistinteRuoliAssociati() throws Exception {
		EntityManager entityManager = EntityManagerUtil.getEntityManager();

		try {

			ruoloDAO.setEntityManager(entityManager);

			return ruoloDAO.allDescrizioniDistinteRuoliAssociati();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EntityManagerUtil.closeEntityManager(entityManager);
		}
	}

}
