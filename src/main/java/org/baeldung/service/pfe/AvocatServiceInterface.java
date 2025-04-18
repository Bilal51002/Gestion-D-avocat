package org.baeldung.service.pfe;


import java.sql.Date;
import java.util.List;

import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.Barreau;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.baeldung.persistence.model.pfe.Client;
import org.baeldung.persistence.model.pfe.Ville;
import org.springframework.web.multipart.MultipartFile;



public interface AvocatServiceInterface {

	Avocat addAvocat(Avocat avocatAj);
	void deleteAvocat(Long id);

	/*
	 * public Avocat save(Avocat a); public Avocat update(Avocat p); public void
	 * delete(Long id);
	 */
	 List<Avocat> findAll();

	 Avocat findById(Long id);
	 List<Barreau> findAllBarreaux();
	 List<Ville> findAllVilles();
	 List<Avocat> findByfirstName(String mc);
	 List<Barreau> findBynomBarreau(String mv);

	List<Client> findAllClient();
	void saveAvocattoDB(MultipartFile file, String firstName, String lastName, String email, String tel, String telfixe,
			String adresse, Date DateCreation, String password,Barreau idBarreau ,String CarteNational);

}
