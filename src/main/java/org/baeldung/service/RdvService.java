package org.baeldung.service;

import org.baeldung.persistence.dao.pfe.RdvRepository;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.baeldung.persistence.model.pfe.Client;
import org.baeldung.persistence.model.pfe.RDV;
import org.baeldung.service.pfe.RdvServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RdvService implements RdvServiceInterface {

    @Autowired
    private RdvRepository rdvRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BureauAvocatService bureauAvocatService;

    @Override
    public List<RDV> findAll() {
        return rdvRepository.findAll();
    }

    @Override
    public RDV findById(Long id) {
        Optional<RDV> rdv = rdvRepository.findById(id);
        return rdv.orElse(null);
    }

    @Override
    public RDV save(RDV rdv) {
        return rdvRepository.save(rdv);
    }

    @Override
    public RDV update(RDV rdv) {
        if (rdv.getId() == null || !rdvRepository.existsById(rdv.getId())) {
            return null;
        }
        return rdvRepository.save(rdv);
    }

    @Override
    public void deleteById(Long id) {
        if (rdvRepository.existsById(id)) {
            rdvRepository.deleteById(id);
        }
    }

    @Override
    public RDV addRdv(RDV rdv, Long clientId, Long bureauId) {
        Client client = clientService.findById(clientId);
        BureauAvocat bureau = bureauAvocatService.findById(bureauId);

        if (client == null || bureau == null) {
            return null;
        }

        rdv.setClient(client);
        rdv.setBureau(bureau);
        return rdvRepository.save(rdv);
    }

    @Override
    public List<RDV> findByClientId(Long clientId) {
        return rdvRepository.findByClientId(clientId);
    }

    @Override
    public List<RDV> findByBureauId(Long bureauId) {
        return rdvRepository.findByBureauId(bureauId);
    }
}
