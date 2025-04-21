package org.baeldung.service.pfe;

import java.util.List;

import org.baeldung.persistence.model.pfe.Dossier;
import org.baeldung.persistence.model.pfe.TypeDossier;

public interface TypeDossierServiceInterface {
	List<TypeDossier> findAllTypeDossier();
	List<TypeDossier> findAll();
	List<TypeDossier> getAllTypeDossier();
}
