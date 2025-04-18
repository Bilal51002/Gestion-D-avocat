
  package org.baeldung.persistence.dao.pfe;
  
  import java.util.List;
  
import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.Barreau;
import org.baeldung.persistence.model.pfe.BureauAvocat; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
  
  @Repository
  public interface AvocatRepository extends JpaRepository<Avocat, Long> {
	  List<Avocat> findByfirstNameContaining(String mc);
	  public Avocat findByfirstName(String name);
  }
 