package org.baeldung.persistence.dao;

import java.util.List;
import java.util.Optional;

import org.baeldung.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    //User findByEmail(String email);
	/*
	 * @Override void delete(User user);
	 */

	void save(org.apache.tomcat.jni.User user);
	User findByEmail(String email);
	Optional<User> findByFirstName(String firstName);
	List<User> findByfirstNameContaining(String mu);
	


}
