package org.baeldung.persistence.dao.pfe;

import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.CalendrieSeance;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface CalendrieSeanceRepository  extends JpaRepository<CalendrieSeance, Long>{

	
}
