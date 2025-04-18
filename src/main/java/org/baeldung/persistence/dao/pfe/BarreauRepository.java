package org.baeldung.persistence.dao.pfe;

import java.util.List;

import org.baeldung.persistence.model.pfe.Barreau;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarreauRepository  extends JpaRepository<Barreau, Long>{
	List<Barreau> findBynomBarreauContaining(String mv);
}
