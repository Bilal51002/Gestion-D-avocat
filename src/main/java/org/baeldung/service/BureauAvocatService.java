package org.baeldung.service;

import java.util.List;
import java.util.Optional;

import org.baeldung.persistence.dao.pfe.AvocatRepository;
import org.baeldung.persistence.dao.pfe.BarreauRepository;
import org.baeldung.persistence.dao.pfe.BureauAvocatRepository;
import org.baeldung.persistence.dao.pfe.VilleRepository;
import org.baeldung.persistence.model.pfe.Barreau;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.baeldung.persistence.model.pfe.Ville;
import org.baeldung.service.pfe.BureauAvocatServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BureauAvocatService implements BureauAvocatServiceInterface {
	@Autowired
	private BureauAvocatRepository bureauAvocatRepository;
	@Autowired
	private BarreauRepository barreauRepository;
	@Autowired
	private VilleRepository villeRepository;

	@Autowired
	private AvocatRepository avocatRepository;


	@Override
	public BureauAvocat addBureauAvocat(BureauAvocat bureauAvocat) {
		return bureauAvocatRepository.save(bureauAvocat);
	}

	@Override
	public List<BureauAvocat> findAll() {
		return bureauAvocatRepository.findAll();
	}

	@Override
	public BureauAvocat findById(Long id) {
		return bureauAvocatRepository.findById(id).get();
	}
	@Override
	public List<BureauAvocat> findByNom(String mc) {
		return bureauAvocatRepository.findByNomContaining(mc);
	}

	@Override
	public List<Barreau> findAllBarreaux() {
		return barreauRepository.findAll();
	}

	@Override
	public List<Ville> findAllVilles() {
		return villeRepository.findAll();
	}


	public Page<BureauAvocat> findAll(Pageable pageable) {
		return bureauAvocatRepository.findAll(pageable);
	}

	public BureauAvocat save(BureauAvocat bureauAvocat) {
		return bureauAvocatRepository.save(bureauAvocat);
	}

	public void delete(BureauAvocat bureauAvocat) {
		bureauAvocatRepository.delete(bureauAvocat);
	}

	public Page<BureauAvocat> search(String query, Pageable pageable) {
		return bureauAvocatRepository.findByNomContainingOrAdresseContaining(query, query, pageable);
	}

	public long count() {
		return bureauAvocatRepository.count();
	}
}
