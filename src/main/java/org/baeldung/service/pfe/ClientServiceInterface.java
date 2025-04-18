package org.baeldung.service.pfe;

import java.sql.Date;
import java.util.List;

import org.baeldung.persistence.model.pfe.Client;
import org.baeldung.persistence.model.pfe.Dossier;
import org.springframework.web.multipart.MultipartFile;

public interface ClientServiceInterface {
	/* Client addClient(Client client); */

           List<Client> findAll();
            Client findById(Long id);
           List<Client> findByfirstName(String cl);
           List<Dossier> findAllDossier();



		void saveClienttoDB(MultipartFile file, String firstName, String lastName, String email, String tel,
				String telfixe, String adresse, Date DateCreation, String password, String CarteNational);
}
