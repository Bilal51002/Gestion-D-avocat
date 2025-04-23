package org.baeldung.persistence.dao.pfe;

import org.baeldung.persistence.model.pfe.RDV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RdvRepository extends JpaRepository<RDV,Long> {
    List<RDV> findRDVById(Long id);

    @Override
    List<RDV> findAll();
    //List<RDV> findRDVByRdvName(String rdvName);
    List<RDV> findByClientId(Long clientId); // Récupérer les RDV d'un client par son ID

    List<RDV> findByBureauId(Long bureauId);
}
