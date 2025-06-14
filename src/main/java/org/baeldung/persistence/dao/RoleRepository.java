package org.baeldung.persistence.dao;

import org.baeldung.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
    @Query("SELECT r FROM Role r JOIN r.secretaires s WHERE s.id = :id")
    List<Role> findRolesBySecretaireId(@Param("id") Long id);

    @Override
    void delete(Role role);

}
