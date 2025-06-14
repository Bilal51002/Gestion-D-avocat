package org.baeldung.persistence.dao;

import java.util.List;
import java.util.Optional;
import org.baeldung.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u FROM User u WHERE u.email = ?1")
	User findByEmail(String email);
	Optional<User> findByFirstName(String firstName);
	List<User> findByRoles_Name(String roleName);
	List<User> findByfirstNameContaining(String mu);


}
