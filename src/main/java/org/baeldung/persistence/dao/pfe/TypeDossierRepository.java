package org.baeldung.persistence.dao.pfe;



import org.baeldung.persistence.model.pfe.TypeDossier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeDossierRepository extends JpaRepository<TypeDossier, Long> {
	
	
	
	TypeDossier findTypeDossierById(Long id );
}
