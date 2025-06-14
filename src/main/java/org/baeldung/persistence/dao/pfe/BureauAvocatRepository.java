package org.baeldung.persistence.dao.pfe;

import java.util.List;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface BureauAvocatRepository extends JpaRepository<BureauAvocat, Long> {
	List<BureauAvocat> findByNomContaining(String mc);
	BureauAvocat findBysecretaire_id(Long id);
	List<BureauAvocat> findByAdresseContaining(String ville);

	Page<BureauAvocat> findByNomContainingOrAdresseContaining(String nom, String adresse, Pageable pageable);
}
