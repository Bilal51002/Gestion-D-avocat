package org.baeldung.persistence.dao.pfe;

import java.util.List;

import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClientRepository  extends JpaRepository<Client, Long> {

	List<Client> findByfirstNameContaining(String cl);

	@Override
	List<Client> findAll();
	Client findByEmail(String firstName);

		Page<Client> findByAvocats(Avocat avocat, Pageable pageable);

		/**
		 * Compte le nombre total de clients d'un avocat
		 * @param avocat L'avocat pour lequel compter les clients
		 * @return Le nombre de clients
		 */
		long countByAvocats(Avocat avocat);

		/**
		 * Recherche des clients par nom, prénom ou email
		 * @param firstName Le prénom ou partie du prénom
		 * @param lastName Le nom ou partie du nom
		 * @param email L'email ou partie de l'email
		 * @param pageable La configuration de pagination
		 * @return Une page de clients correspondant à la recherche
		 */
		Page<Client> findByFirstNameContainingOrLastNameContainingOrEmailContaining(
				String firstName, String lastName, String email, Pageable pageable);

		/**
		 * Recherche des clients d'un avocat spécifique par nom, prénom ou email
		 * @param avocat1 L'avocat pour lequel chercher les clients (pour firstName)
		 * @param firstName Le prénom ou partie du prénom
		 * @param avocat2 L'avocat pour lequel chercher les clients (pour lastName)
		 * @param lastName Le nom ou partie du nom
		 * @param avocat3 L'avocat pour lequel chercher les clients (pour email)
		 * @param email L'email ou partie de l'email
		 * @param pageable La configuration de pagination
		 * @return Une page de clients correspondant à la recherche
		 */
		Page<Client> findByAvocatsAndFirstNameContainingOrAvocatsAndLastNameContainingOrAvocatsAndEmailContaining(
				Avocat avocat1, String firstName,
				Avocat avocat2, String lastName,
				Avocat avocat3, String email,
				Pageable pageable);

		@Query("SELECT c FROM Client c WHERE " +
				"LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
				"LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
				"LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%'))")
		Page<Client> search(@Param("query") String query, Pageable pageable);

		/**
		 * Méthode alternative pour rechercher des clients d'un avocat spécifique
		 * @param avocatId L'ID de l'avocat
		 * @param query Le terme de recherche
		 * @param pageable La configuration de pagination
		 * @return Une page de clients correspondant à la recherche
		 */
		@Query("SELECT c FROM Client c JOIN c.avocats a WHERE a.id = :avocatId AND (" +
				"LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
				"LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
				"LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')))")
		Page<Client> searchByAvocat(@Param("avocatId") Long avocatId, @Param("query") String query, Pageable pageable);

	//Client getRDV();

}
