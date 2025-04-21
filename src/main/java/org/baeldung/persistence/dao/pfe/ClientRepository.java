package org.baeldung.persistence.dao.pfe;

import java.util.List;

import org.baeldung.persistence.model.pfe.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClientRepository  extends JpaRepository<Client, Long> {

	List<Client> findByfirstNameContaining(String cl);

	@Override
	List<Client> findAll();
	Client findByEmail(String firstName);
	//Client getRDV();
}
