package org.baeldung.persistence.dao.pfe;

import java.util.List;

import org.baeldung.persistence.model.pfe.Barreau;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface BureauAvocatRepository extends JpaRepository<BureauAvocat, Long> {
	List<BureauAvocat> findByNomContaining(String mc);
	/* List<BureauAvocat> */
}
