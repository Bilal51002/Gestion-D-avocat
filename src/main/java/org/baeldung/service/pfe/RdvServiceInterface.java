package org.baeldung.service.pfe;

import org.baeldung.persistence.model.pfe.RDV;

import java.util.List;

public interface RdvServiceInterface {
    List<RDV> findAll();
    RDV findById(Long id);
    RDV save(RDV rdv);
    RDV update(RDV rdv);
    void deleteById(Long id);
    RDV addRdv(RDV rdv, Long clientId, Long bureauId);
    List<RDV> findByClientId(Long clientId);
    List<RDV> findByBureauId(Long bureauId);
}
