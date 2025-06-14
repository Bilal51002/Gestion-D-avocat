package org.baeldung.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import org.baeldung.persistence.dao.pfe.CalendrieSeanceRepository;
import org.baeldung.persistence.dao.pfe.DossierRepository;
import org.baeldung.persistence.dao.pfe.TribunalRepository;
import org.baeldung.persistence.model.pfe.*;
import org.baeldung.service.pfe.DossierServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DossierService implements DossierServiceInterface {
	@Autowired
	private DossierRepository dossierRepository;

	@Autowired
	private TribunalRepository tribunalRepository;
	@Autowired
	private EntityManager entityManager;


	@Autowired
	private CalendrieSeanceRepository calendrieSeanceRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Tribunal> findAllnom() {
		return tribunalRepository.findAll();
	}
	public List<Dossier> getDossiersByNumeroDossierStartingWith(String prefix) {
		return dossierRepository.findByNumeroDossierStartingWith(prefix);
	}

	@Transactional(readOnly = true)
	public List<Dossier> findDossiersByClientId(Long clientId) {
		// Vous pouvez utiliser une requête JPQL ou Criteria API
		// Voici un exemple avec JPQL
		return entityManager.createQuery(
						"SELECT d FROM Dossier d JOIN d.client c WHERE c.id = :clientId", Dossier.class)
				.setParameter("clientId", clientId)
				.getResultList();
	}

	public Page<Dossier> findDossiersByClientIdPaginated(Long clientId, Pageable pageable) {
		// Get all dossiers for the client using your existing method
		List<Dossier> allDossiers = findDossiersByClientId(clientId);

		// Manual pagination
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), allDossiers.size());

		// Apply sorting if needed
		if (pageable.getSort().isSorted()) {
			if (pageable.getSort().getOrderFor("DateCreation") != null) {
				if (pageable.getSort().getOrderFor("DateCreation").isDescending()) {
					allDossiers.sort((d1, d2) -> {
						// Handle null dates safely
						if (d1.getDateCreation() == null) return 1;
						if (d2.getDateCreation() == null) return -1;
						return d2.getDateCreation().compareTo(d1.getDateCreation());
					});
				} else {
					allDossiers.sort((d1, d2) -> {
						// Handle null dates safely
						if (d1.getDateCreation() == null) return -1;
						if (d2.getDateCreation() == null) return 1;
						return d1.getDateCreation().compareTo(d2.getDateCreation());
					});
				}
			}
		}

		// Extract the sublist according to pagination parameters
		// Check if list is empty first to avoid IndexOutOfBoundsException
		List<Dossier> paginatedDossiers = allDossiers.isEmpty() ?
				allDossiers :
				allDossiers.subList(start, end);

		// Create and return the page
		return new PageImpl<>(paginatedDossiers, pageable, allDossiers.size());
	}
	public List<Dossier> getDossiersByBureau(BureauAvocat bureau) {
		return dossierRepository.findByBureau(bureau);
	}

	public Dossier updateDossier(Dossier dossier) {
		return dossierRepository.save(dossier);
	}

	public Page<Dossier> findByAvocat(Avocat avocat, Pageable pageable) {
		// Implémentation selon votre modèle de données
		return dossierRepository.findByAvocat(avocat, pageable);
	}

	public List<Dossier> getDossiersBySecretaire(Secretaire secretaire) {
		BureauAvocat bureau = secretaire.getBureau();
		if (bureau == null) {
			return Collections.emptyList(); // Retourne une liste vide si aucun bureau n'est associé
		}
		return getDossiersByBureau(bureau);
	}

	public Page<Dossier> getDossiersByBureauPaginated(BureauAvocat bureau, Pageable pageable) {
		return dossierRepository.findByBureau(bureau, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Dossier> findAll() {
		return dossierRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Dossier> findBynumeroDossier(String md) {
		return dossierRepository.findBynumeroDossierContaining(md);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Dossier> getAllDossier() {
		return dossierRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Dossier getDossierById(Long id) {
		return dossierRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Dossier non trouvé"));
	}

	@Override
	public void deleteDossier(Long id) {
		dossierRepository.deleteById(id);
	}

	@Override
	public Dossier addDossier(Dossier dossierAj) {
		return dossierRepository.save(dossierAj);
	}

	@Override
	public CalendrieSeance addCalendrieSeance(CalendrieSeance dossierAjou) {
		return calendrieSeanceRepository.save(dossierAjou);
	}
		public long countByAvocat(Avocat avocat) {
			return dossierRepository.countByAvocat(avocat);
		}

		public long countActiveByAvocat(Avocat avocat) {
			return dossierRepository.countByAvocatAndTypeDecas("قيد التنفيذ", avocat);
		}

		public Optional<Dossier> findById(Long id) {
			return dossierRepository.findById(id);
		}

		public Optional<Dossier> findByNumeroDossier(String numero) {
			return dossierRepository.findByNumeroDossier(numero);
		}

		public Dossier save(Dossier dossier) {
			return dossierRepository.save(dossier);
		}

		public void delete(Dossier dossier) {
			dossierRepository.delete(dossier);
		}

		public Page<Dossier> search(String query, Pageable pageable) {
			return dossierRepository.findByNumeroDossierContainingOrSujetContaining(query, query, pageable);
		}

		public Page<Dossier> searchByAvocat(Avocat avocat, String query, Pageable pageable) {
			return dossierRepository.findByAvocatAndNumeroDossierContainingOrAvocatAndSujetContaining(
					avocat, query, avocat, query, pageable);
		}

}