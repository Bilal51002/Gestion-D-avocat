package org.baeldung.service.pfe;

import java.util.List;

import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.Barreau;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.baeldung.persistence.model.pfe.Ville;

public interface BureauAvocatServiceInterface {

	BureauAvocat addBureauAvocat(BureauAvocat bureauAvocat);
	List<BureauAvocat> findAll();
	List<BureauAvocat> findByNom(String mc);
	BureauAvocat findById(Long id);
	List<Barreau> findAllBarreaux();
	List<Ville> findAllVilles();
	/* List<Avocat> findByName(); */
}
