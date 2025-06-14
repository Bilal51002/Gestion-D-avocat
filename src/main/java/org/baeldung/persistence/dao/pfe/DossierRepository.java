package org.baeldung.persistence.dao.pfe;

import java.util.List;
import java.util.Optional;


import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.baeldung.persistence.model.pfe.Dossier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface DossierRepository extends JpaRepository<Dossier, Long> {
	// Dans DossierRepository
	@Query("SELECT d FROM Dossier d JOIN d.client c WHERE c.id = :clientId")
	List<Dossier> findByClientId(@Param("clientId") Long clientId);
	List<Dossier> findByBureau(BureauAvocat bureau);
	List<Dossier> findByNumeroDossierStartingWith(String prefix);
	Page<Dossier> findByAvocat(Avocat avocat, Pageable pageable);
	// In your DossierRepository interface
	@Query("SELECT d FROM Dossier d JOIN d.client c WHERE c.id = :clientId")
	Page<Dossier> findByClientId(@Param("clientId") Long clientId, Pageable pageable);
	Page<Dossier> findByBureau(BureauAvocat bureau, Pageable pageable);
	@Query("SELECT d FROM Dossier d LEFT JOIN FETCH d.client WHERE d.id = :id")
	Dossier findByIdWithClient(@Param("id") Long id);

	@Query("SELECT d FROM Dossier d LEFT JOIN FETCH d.client")
	List<Dossier> findAllWithClient();
	/*@Query("SELECT d FROM Dossier d WHERE DATEDIFF(d.dateSeance, SYSDATE()) BETWEEN 0 AND 6 ORDER BY d.dateSeance")
	public List<Dossier> getWeekSeances();
	
	@Query("SELECT d FROM Dossier d WHERE DATEDIFF(SYSDATE(), d.dateSeance) = 0 ORDER BY d.dateSeance")
	public List<Dossier> getDaySeances();
	
	public Dossier findFirstByOrderByNumeroDossierDesc();
	
	public Dossier findFirstByOrderByDateSeance();*/
	
	/* List<Dossier> findBynumeroNationalContaining(String mn); */
	 List<Dossier> findBynumeroDossierContaining(String md);
		/* List<Dossier> findBynumeroDossierOrnumeroNationalContaining(String md, String mn); */

	Optional<Dossier> findByNumeroDossier(String numeroDossier);

	/**
	 * Trouve tous les dossiers d'un avocat avec pagination
	 * @param avocat L'avocat pour lequel chercher les dossiers
	 * @param pageable La configuration de pagination
	 * @return Une page de dossiers
	 */


	/**
	 * Compte le nombre total de dossiers d'un avocat
	 * @param avocat L'avocat pour lequel compter les dossiers
	 * @return Le nombre de dossiers
	 */
	@Query("SELECT COUNT(d) FROM Dossier d JOIN d.avocat a WHERE a = :avocat")
	long countByAvocat(@Param("avocat") Avocat avocat);

	/**
	 * Compte le nombre de dossiers d'un type spécifique pour un avocat
	 * @param typeDecas Le type de dossier
	 * @param avocat L'avocat pour lequel compter les dossiers
	 * @return Le nombre de dossiers du type spécifié
	 */
	@Query("SELECT COUNT(d) FROM Dossier d JOIN d.avocat a WHERE d.typeDecas = :typeDecas AND a = :avocat")
	long countByAvocatAndTypeDecas(@Param("typeDecas") String typeDecas, @Param("avocat") Avocat avocat);

	/**
	 * Recherche des dossiers par numéro ou sujet
	 * @param numeroDossier Le numéro ou partie du numéro du dossier
	 * @param sujet Le sujet ou partie du sujet du dossier
	 * @param pageable La configuration de pagination
	 * @return Une page de dossiers correspondant à la recherche
	 */
	Page<Dossier> findByNumeroDossierContainingOrSujetContaining(
			String numeroDossier, String sujet, Pageable pageable);

	/**
	 * Recherche des dossiers d'un avocat spécifique par numéro ou sujet
	 * @param avocat1 L'avocat pour lequel chercher les dossiers (pour numeroDossier)
	 * @param numeroDossier Le numéro ou partie du numéro du dossier
	 * @param avocat2 L'avocat pour lequel chercher les dossiers (pour sujet)
	 * @param sujet Le sujet ou partie du sujet du dossier
	 * @param pageable La configuration de pagination
	 * @return Une page de dossiers correspondant à la recherche
	 */
	@Query("SELECT d FROM Dossier d JOIN d.avocat a WHERE a = :avocat1 AND d.numeroDossier LIKE %:numeroDossier% OR a = :avocat2 AND d.sujet LIKE %:sujet%")
	Page<Dossier> findByAvocatAndNumeroDossierContainingOrAvocatAndSujetContaining(
			@Param("avocat1") Avocat avocat1, @Param("numeroDossier") String numeroDossier,
			@Param("avocat2") Avocat avocat2, @Param("sujet") String sujet,
			Pageable pageable);

	/**
	 * Méthode alternative pour rechercher des dossiers par terme de recherche
	 * @param query Le terme de recherche
	 * @param pageable La configuration de pagination
	 * @return Une page de dossiers correspondant à la recherche
	 */
	@Query("SELECT d FROM Dossier d WHERE " +
			"LOWER(d.numeroDossier) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(d.sujet) LIKE LOWER(CONCAT('%', :query, '%'))")
	Page<Dossier> search(@Param("query") String query, Pageable pageable);

	/**
	 * Méthode alternative pour rechercher des dossiers d'un avocat spécifique
	 * @param avocatId L'ID de l'avocat
	 * @param query Le terme de recherche
	 * @param pageable La configuration de pagination
	 * @return Une page de dossiers correspondant à la recherche
	 */
	@Query("SELECT d FROM Dossier d JOIN d.avocat a WHERE a.id = :avocatId AND (" +
			"LOWER(d.numeroDossier) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(d.sujet) LIKE LOWER(CONCAT('%', :query, '%')))")
	Page<Dossier> searchByAvocat(@Param("avocatId") Long avocatId, @Param("query") String query, Pageable pageable);


	
}
