package org.baeldung.persistence.dao.pfe;

import org.baeldung.persistence.model.pfe.Secretaire;
import org.baeldung.persistence.model.pfe.Tribunal;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface TribunalRepository extends JpaRepository<Tribunal, Long> { 

}
