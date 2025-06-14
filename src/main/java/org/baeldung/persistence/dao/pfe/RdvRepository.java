package org.baeldung.persistence.dao.pfe;

import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.baeldung.persistence.model.pfe.RDV;
import org.baeldung.persistence.model.pfe.Statut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RdvRepository extends JpaRepository<RDV,Long> {
 // Dans RdvRepository
 @Query("SELECT r FROM RDV r WHERE r.id = :id")
 Optional<RDV> findRdvByIdValue(@Param("id") Long id);// List<RDV> findRdvById(Long id);
    @Query("SELECT r FROM RDV r WHERE r.bureau.id = :bureauId")
    List<RDV> findByBureauId(@Param("bureauId") Long bureauId);
    List<RDV> findByAvocatIdAndDateBetween(Long avocatId, LocalDate start, LocalDate end);
    RDV findFirstByClientIdAndDateAfterOrderByDateAsc(Long clientId, LocalDate date);
    // Dans RdvRepository.java
    List<RDV> findByClientIdOrderByDateDesc(Long clientId);
    List<RDV> findByAvocatIdAndDate(Long avocatId, LocalDate date);

   Page<RDV> findByBureau(BureauAvocat bureau, Pageable pageable);

   // Autres méthodes de requête personnalisées si nécessaire
   List<RDV> findByBureau(BureauAvocat bureau);

   // Exemple de méthodes supplémentaires potentiellement utiles
   Page<RDV> findByBureauAndStatut(BureauAvocat bureau, String statut, Pageable pageable);
    long countByBureauAndDate(BureauAvocat bureau, LocalDate date);

    // Autres méthodes de requête personnalisées si nécessaire
    // Par exemple :
    List<RDV> findByBureauAndDate(BureauAvocat bureau, LocalDate date);

    @Query("SELECT r FROM RDV r WHERE r.bureau.id = :bureauId AND r.date BETWEEN :startDate AND :endDate")
    List<RDV> findByBureauIdAndDateBetween(
            @Param("bureauId") Long bureauId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Nouvelle méthode pour trouver les RDV d'un bureau à une date spécifique
    @Query("SELECT r FROM RDV r WHERE r.bureau.id = :bureauId AND r.date = :date")
    List<RDV> findByBureauIdAndDate(
            @Param("bureauId") Long bureauId,
            @Param("date") LocalDate date
    );

    // Méthode pour trouver les RDV à une date spécifique (utilisée dans envoyerRappelsRendezVous())
    List<RDV> findByDate(LocalDate date);

    // Si vous avez besoin de plus de flexibilité, vous pouvez utiliser des méthodes avec des critères plus spécifiques
    long countByBureauAndDateAndStatut(BureauAvocat bureau, LocalDate date, String statut);
   // Compter le nombre de RDV pour un bureau
   long countByBureau(BureauAvocat bureau);
    @Query("SELECT COUNT(r) FROM RDV r WHERE CAST(r.date AS LocalDate) = :today")
    long countByDateEquals(@Param("today") LocalDate today);

    Page<RDV> findByClientId(Long clientId, Pageable pageable);
    @Override
    List<RDV> findAll();

    @Query("SELECT COUNT(r) FROM RDV r WHERE r.client.id = :clientId")
    long countByClientId(@Param("clientId") Long clientId);
    //List<RDV> findRDVByRdvName(String rdvName);
    List<RDV> findByClientId(Long clientId); // Récupérer les RDV d'un client par son ID

   // List<RDV> findByBureauId(Long bureauId);

        // Trouver les rendez-vous pour un avocat spécifique
        Page<RDV> findByAvocat(Avocat avocat, Pageable pageable);

        // Trouver les rendez-vous pour un avocat spécifique avec un statut spécifique
        Page<RDV> findByAvocatAndStatut(Avocat avocat, Statut statut, Pageable pageable);

        // Compter les rendez-vous pour un avocat
        long countByAvocat(Avocat avocat);

        // Compter les rendez-vous pour un avocat avec un statut spécifique
        long countByAvocatAndStatut(Avocat avocat, Statut statut);

        // Trouver le prochain rendez-vous pour un avocat
        @Query("SELECT r FROM RDV r WHERE r.avocat = :avocat AND r.date >= CURRENT_DATE ORDER BY r.date ASC, r.heur ASC")
        List<RDV> findNextByAvocat(@Param("avocat") Avocat avocat, Pageable pageable);

        // Recherche de rendez-vous par sujet ou par client pour un avocat
        @Query("SELECT r FROM RDV r WHERE r.avocat = :avocat AND " +
                "(LOWER(r.sujet) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(r.client.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(r.client.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
        List<RDV> search(@Param("avocat") Avocat avocat, @Param("query") String query);

        // Recherche avancée avec filtres
        @Query("SELECT r FROM RDV r WHERE r.avocat = :avocat AND " +
                "(:statut IS NULL OR r.statut = :statut) AND " +
                "(:fromDate IS NULL OR r.date >= :fromDate) AND " +
                "(:toDate IS NULL OR r.date <= :toDate) AND " +
                "(LOWER(r.sujet) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(r.client.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(r.client.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
        List<RDV> search(@Param("avocat") Avocat avocat,
                         @Param("query") String query,
                         @Param("statut") Statut statut,
                         @Param("fromDate") LocalDate fromDate,
                         @Param("toDate") LocalDate toDate);

    Optional<RDV> findFirstByAvocatAndDateAfterOrderByDateAsc(Avocat avocat, LocalDate date);
    @Query("SELECT r FROM RDV r LEFT JOIN r.client c WHERE " +
            "LOWER(r.sujet) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<RDV> search(@Param("query") String query, Pageable pageable);

    /**
     * Recherche des rendez-vous d'un avocat spécifique par sujet ou nom de client
     * @param avocatId L'ID de l'avocat
     * @param query Le terme de recherche
     * @param pageable La configuration de pagination
     * @return Une page de rendez-vous correspondant à la recherche
     */
    @Query("SELECT r FROM RDV r LEFT JOIN r.client c WHERE r.avocat.id = :avocatId AND (" +
            "LOWER(r.sujet) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<RDV> searchByAvocat(@Param("avocatId") Long avocatId, @Param("query") String query, Pageable pageable);

}
