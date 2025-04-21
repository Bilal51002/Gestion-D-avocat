package org.baeldung.service.pfe;

import org.baeldung.persistence.model.pfe.RDV;

import java.util.List;

public interface RdvServiceInterface {
    List<RDV> findAll ();
    RDV findById (Long id);
    RDV save (RDV rdv);
    RDV update (RDV rdv);
    RDV delete (RDV rdv);
    RDV Add (RDV rdv);
}
