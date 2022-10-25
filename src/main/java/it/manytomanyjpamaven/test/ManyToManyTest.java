package it.manytomanyjpamaven.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.RuntimeErrorException;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.exception.UtenteConRuoliAssociatiException;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.StatoUtente;
import it.manytomanyjpamaven.model.Utente;
import it.manytomanyjpamaven.service.MyServiceFactory;
import it.manytomanyjpamaven.service.RuoloService;
import it.manytomanyjpamaven.service.UtenteService;

public class ManyToManyTest {

	public static void main(String[] args) {
		UtenteService utenteServiceInstance = MyServiceFactory.getUtenteServiceInstance();
		RuoloService ruoloServiceInstance = MyServiceFactory.getRuoloServiceInstance();

		// ora passo alle operazioni CRUD
		try {

			// inizializzo i ruoli sul db
			initRuoli(ruoloServiceInstance);

			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testInserisciNuovoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testCollegaUtenteARuoloEsistente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testModificaStatoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			System.out.println("In tabella Ruolo ci sono " + ruoloServiceInstance.listAll().size() + " elementi.");
			
			testRimuoviRuoloDaUtente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			System.out.println("In tabella Ruolo ci sono " + ruoloServiceInstance.listAll().size() + " elementi.");
			
			testTuttiUtentiCreatiNelMeseDi(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testTuttiUtentiConPasswordLungaMenoDi8Caratteri(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testCiSonoAdminTraUtentiDisabilitati(utenteServiceInstance,ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
					
			testQuantiUtentiAdmin(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testTutteLeDescrizioniDistinteRuoliAssociati(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Ruolo ci sono " + ruoloServiceInstance.listAll().size() + " elementi.");
			
			testRimuoviRuoloSoloSeNessunUtente(ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			System.out.println("In tabella Ruolo ci sono " + ruoloServiceInstance.listAll().size() + " elementi.");
			
			testRimuoviRuolo(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			System.out.println("In tabella Ruolo ci sono " + ruoloServiceInstance.listAll().size() + " elementi.");
			
			testAggiornaRuolo(ruoloServiceInstance);
			System.out.println("In tabella Ruolo ci sono " + ruoloServiceInstance.listAll().size() + " elementi.");
			
			System.out.println("Al momento ci sono "+ruoloServiceInstance.listAll().size()+" ruoli e "+utenteServiceInstance.listAll().size()+" utenti nei DB.");
			cleanUp(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("Al momento ci sono "+ruoloServiceInstance.listAll().size()+" ruoli e "+utenteServiceInstance.listAll().size()+" utenti nei DB.");

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void initRuoli(RuoloService ruoloServiceInstance) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", "ROLE_ADMIN"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", "ROLE_CLASSIC_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", "ROLE_CLASSIC_USER"));
		}
	}

	private static void testInserisciNuovoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testInserisciNuovoUtente inizio.............");

		Utente utenteNuovo = new Utente("pippo.rossi", "xxx", "pippo", "rossi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		System.out.println(".......testInserisciNuovoUtente fine: PASSED.............");
	}

	private static void testCollegaUtenteARuoloEsistente(RuoloService ruoloServiceInstance,
			UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testCollegaUtenteARuoloEsistente inizio.............");

		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testCollegaUtenteARuoloEsistente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario.bianchi", "JJJ", "mario", "bianchi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito: utente non inserito ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// per fare il test ricarico interamente l'oggetto e la relazione
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (utenteReloaded.getRuoli().size() != 1)
			throw new RuntimeException("testInserisciNuovoUtente fallito: ruoli non aggiunti ");

		System.out.println(".......testCollegaUtenteARuoloEsistente fine: PASSED.............");
	}

	private static void testModificaStatoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testModificaStatoUtente inizio.............");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testModificaStatoUtente fallito: utente non inserito ");

		// proviamo a passarlo nello stato DISABILITATO ma salviamoci il vecchio stato
		StatoUtente vecchioStato = utenteNuovo.getStato();
		utenteNuovo.setStato(StatoUtente.DISABILITATO);
		utenteServiceInstance.aggiorna(utenteNuovo);

		if (utenteNuovo.getStato().equals(vecchioStato))
			throw new RuntimeException("testModificaStatoUtente fallito: modifica non avvenuta correttamente ");

		System.out.println(".......testModificaStatoUtente fine: PASSED.............");
	}

	private static void testRimuoviRuoloDaUtente(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testRimuoviRuoloDaUtente inizio.............");

		// carico un ruolo e lo associo ad un nuovo utente
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("aldo.manuzzi", "pwd@2", "aldo", "manuzzi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente non inserito ");
		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);

		// ora ricarico il record e provo a disassociare il ruolo
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		boolean confermoRuoloPresente = false;
		for (Ruolo ruoloItem : utenteReloaded.getRuoli()) {
			if (ruoloItem.getCodice().equals(ruoloEsistenteSuDb.getCodice())) {
				confermoRuoloPresente = true;
				break;
			}
		}

		if (!confermoRuoloPresente)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente e ruolo non associati ");

		// ora provo la rimozione vera e propria ma poi forzo il caricamento per fare un
		// confronto 'pulito'
		utenteServiceInstance.rimuoviRuoloDaUtente(utenteReloaded.getId(), ruoloEsistenteSuDb.getId());
		utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (!utenteReloaded.getRuoli().isEmpty())
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo ancora associato ");

		System.out.println(".......testRimuoviRuoloDaUtente fine: PASSED.............");
	}

	private static void testAggiornaRuolo(RuoloService ruoloService) throws Exception {
		System.out.println(".......testAggiornaRuolo inizio.............");
		if (ruoloService.listAll().isEmpty())
			throw new RuntimeException("FAILED : non ci sono ruoli nel DB.");
		Ruolo ruoloAggiornato = new Ruolo("Moderator", "ROLE_MODERATOR");
		ruoloAggiornato.setId(ruoloService.listAll().get(0).getId());
		ruoloService.aggiorna(ruoloAggiornato);
		if (!ruoloService.listAll().get(0).getDescrizione().equals(ruoloAggiornato.getDescrizione()))
			throw new RuntimeException("FAILED : il ruolo non e' stato aggiornato.");
		System.out.println(".......testAggiornaRuolo fine: PASSED.............");
	}

	private static void testRimuoviRuolo(RuoloService ruoloService, UtenteService utenteService) throws Exception {
		System.out.println(".......testRimuoviRuolo inizio.............");
		if (ruoloService.listAll().isEmpty())
			throw new RuntimeException("FAILED : non ci sono ruoli nel DB.");
		
		int oldSize = ruoloService.listAll().size();
		ruoloService.rimuovi(ruoloService.listAll().get(0).getId());
		if(oldSize <= ruoloService.listAll().size())
			throw new RuntimeException("FAILED : il ruolo non e' stato eliminato.");
		System.out.println(".......testRimuoviRuolo fine: PASSED.............");
	}

	private static void testRimuoviRuoloSoloSeNessunUtente(RuoloService ruoloService)throws Exception{
		System.out.println(".......testRimuoviRuoloSoloSeNessunUtente inizio.............");
		if (ruoloService.listAll().isEmpty())
			throw new RuntimeException("FAILED : non ci sono ruoli nel DB.");
		try {
			ruoloService.rimuoviSoloSeNessunUtente(ruoloService.listAll().get(0).getId());
		} catch (UtenteConRuoliAssociatiException e) {
			
		}
		System.out.println(".......testRimuoviRuoloSoloSeNessunUtente fine: PASSED.............");
	}
	
	private static void testTuttiUtentiCreatiNelMeseDi(UtenteService utenteService)throws Exception{
		System.out.println(".......testTuttiUtentiCreatiNelMeseDi inizio.............");
		if(utenteService.listAll().isEmpty())
			throw new RuntimeException("FAILED : non ci sono utenti nel DB.");
		List<Utente> result = utenteService.tuttiUtentiCreatiNelMeseDi(new Date());
		if(result.isEmpty())
			throw new RuntimeException("FAILED : la ricerca non ha dato i isultati attesi.");
		System.out.println(".......testTuttiUtentiCreatiNelMeseDi fine: PASSED.............");
	}
	
	private static void testTuttiUtentiConPasswordLungaMenoDi8Caratteri(UtenteService utenteService)throws Exception{
		System.out.println(".......testTuttiUtentiConPasswordLungaMenoDi8Caratteri inizio.............");
		if(utenteService.listAll().isEmpty())
			throw new RuntimeException("FAILED : non ci sono utenti nel DB.");
		List<Utente> result = utenteService.tuttiUtentiConPasswordLungaMenoDi8Caratteri();
		if(result.isEmpty())
			throw new RuntimeException("FAILED : la ricerca non ha dato i isultati attesi.");
		System.out.println(".......testTuttiUtentiConPasswordLungaMenoDi8Caratteri fine: PASSED.............");
	}
	
	private static void testCiSonoAdminTraUtentiDisabilitati(UtenteService utenteService, RuoloService ruoloService)throws Exception{
		System.out.println(".......testCiSonoAdminTraUtentiDisabilitati inizio.............");
		Utente utenteDisabilitato = new Utente("mattiMax", "wrek", "Mattia", "Massimo", new Date());
		utenteDisabilitato.setStato(StatoUtente.DISABILITATO);
		utenteDisabilitato.getRuoli().add(ruoloService.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN"));
		utenteService.inserisciNuovo(utenteDisabilitato);		
		boolean result = utenteService.ciSonoAdminTraUtentiDisabilitati();
		if(!result)
			throw new RuntimeException("FAILED : la ricerca non ha dato i isultati attesi.");
		System.out.println(".......testCiSonoAdminTraUtentiDisabilitati fine: PASSED.............");
	}
	
	private static void testQuantiUtentiAdmin(RuoloService ruoloService,UtenteService utenteService)throws Exception{
		System.out.println(".......testQuantiUtentiAdmin inizio.............");
		if(utenteService.listAll().isEmpty())
			throw new RuntimeException("FAILED : non ci sono utenti nel DB.");
		Long result = ruoloService.quantiUtentiAdmin();
		if(result<1)
			throw new RuntimeException("FAILED : la ricerca non ha dato risultati attendibili.");
		System.out.println(".......testQuantiUtentiAdmin fine: PASSED.............");
	}
	
	private static void testTutteLeDescrizioniDistinteRuoliAssociati(RuoloService ruoloService, UtenteService utenteService) throws Exception {
		System.out.println(".......testQuantiUtentiAdmin inizio.............");
		if(utenteService.listAll().isEmpty() || ruoloService.listAll().isEmpty())
			throw new RuntimeException("FAILED : uno o piu' DB vuoti.");
		List<String> result = ruoloService.tutteLeDescrizioniDistinteRuoliAssociati();
		if(result.isEmpty())
			throw new RuntimeException("FAILED : la ricerca non ha dato risultati attendibili.");
		System.out.println(".......testQuantiUtentiAdmin fine: PASSED.............");
	}
	
	private static void cleanUp(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)throws Exception{
		while(!ruoloServiceInstance.listAll().isEmpty()) {
			ruoloServiceInstance.rimuovi(ruoloServiceInstance.listAll().get(0).getId());
		}
		List<Utente> daEliminare = utenteServiceInstance.listAll();
		for (Utente utente : daEliminare) {
			utenteServiceInstance.rimuovi(utente.getId());
		}
	}
}
