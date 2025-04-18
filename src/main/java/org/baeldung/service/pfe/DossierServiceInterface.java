package org.baeldung.service.pfe;

import java.sql.Date;
import java.util.List;

import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.CalendrieSeance;
import org.baeldung.persistence.model.pfe.Dossier;
import org.baeldung.persistence.model.pfe.Tribunal;
import org.baeldung.persistence.model.pfe.TypeDossier;


public interface DossierServiceInterface {
	 Dossier addDossier(Dossier dossierAj);
		List<Dossier> findAll();
		/*List<Dossier> findBynumeroDossierOrnumeroNational(String md,String mn);*/
		List<Dossier> findBynumeroDossier(String md);

		/* List<Dossier> findBynumeroNational(String mn); */
		List<Tribunal> findAllnom();
		List<Dossier> getAllDossier();
		Dossier getDossierById(Long id);


		/* void deleteDossier(String numeroDossier); */
		void deleteDossier(Long id);
		CalendrieSeance addCalendrieSeance(CalendrieSeance dossierAjou);
		/*
		 * void saveDossiertoDB(String numeroDossier, Date DateCreation, String
		 * typeDecas, String typeProsedure, Date dateProchSession, String
		 * numeroNational, TypeDossier id, String sujet);
		 */

	  }

