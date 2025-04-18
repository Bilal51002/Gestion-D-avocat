package org.baeldung.service.pfe;


import org.baeldung.persistence.model.pfe.Barreau;
import org.baeldung.persistence.model.pfe.Dossier;
import org.baeldung.persistence.model.pfe.Secretaire;
import org.baeldung.persistence.model.pfe.Ville;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;

public interface SecretaireServiceInterface {
    Secretaire addSecretaire(Secretaire secretaire);
	Secretaire findByEmail(String email);

    List<Secretaire> findAll();
    Secretaire findById(Long id);
    List<Secretaire> findByfirstName(String mt);

	 List<Barreau> findAllBarreaux();
	 List<Ville> findAllVilles();
	 List<Barreau> findBynomBarreau(String mv);
	    List<Dossier> findAllDossier();
		void deleteSecretaire(Long id);
		void saveSecretairetoDB(MultipartFile file, String firstName, String lastName, String email, String tel,
				String telfixe, String adresse, Date DateCreation,String password, String CarteNational);
}
