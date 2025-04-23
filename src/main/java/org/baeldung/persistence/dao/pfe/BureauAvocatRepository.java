package org.baeldung.persistence.dao.pfe;

import java.util.List;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface BureauAvocatRepository extends JpaRepository<BureauAvocat, Long> {
	List<BureauAvocat> findByNomContaining(String mc);
	/* List<BureauAvocat> */
	// Recherche des bureaux d'avocats par nom
	//List<BureauAvocat> findByNomContaining(String nom);

	// Recherche des bureaux d'avocats par barreau
	//List<BureauAvocat> findByBarreauNomBarreauContaining(String nomBarreau);

	// Recherche des bureaux d'avocats par ville (adresse)
	List<BureauAvocat> findByAdresseContaining(String ville);
}
