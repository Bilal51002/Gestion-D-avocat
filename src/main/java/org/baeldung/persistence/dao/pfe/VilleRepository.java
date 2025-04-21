package org.baeldung.persistence.dao.pfe;

import org.baeldung.persistence.model.pfe.Ville;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VilleRepository extends JpaRepository<Ville, Long> {

}
