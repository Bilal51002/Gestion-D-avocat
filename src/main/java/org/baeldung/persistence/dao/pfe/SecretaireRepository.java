package org.baeldung.persistence.dao.pfe;

import java.util.List;

import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.Secretaire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  SecretaireRepository extends JpaRepository<Secretaire, Long> {
	  List<Secretaire> findByfirstNameContaining(String mt);
	  Secretaire findByEmail(String email);
	  
}

