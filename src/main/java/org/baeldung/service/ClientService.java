package org.baeldung.service;

import java.io.IOException;
import java.sql.Date;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;

import org.baeldung.persistence.dao.pfe.ClientRepository;
import org.baeldung.persistence.dao.pfe.DossierRepository;
import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.service.pfe.ClientServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.baeldung.persistence.model.pfe.Client;
import org.baeldung.persistence.model.pfe.Dossier;

@Service
public class ClientService implements ClientServiceInterface {
	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private DossierRepository dossierRepository;

	public Collection<Client> findAllById(List<Long> ids) {
		return clientRepository.findAllById(ids);
	}

	@Override
	public void saveClienttoDB(MultipartFile file,
							   String firstName,
							   String lastName,
							   String email,
							   String tel,
							   String telfixe,
							   String adresse,
							   Date DateCreation,
							   String password,
							   String CarteNational) {

		Client client = new Client();
		if (file != null && !file.isEmpty()) {
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			if (fileName.contains("..")) {
				throw new IllegalArgumentException("Nom de fichier invalide: " + fileName);
			}
			try {
				client.setImguser(Base64.getEncoder().encodeToString(file.getBytes()));
			} catch (IOException e) {
				throw new RuntimeException("Échec de l'enregistrement du fichier", e);
			}
		}

		client.setFirstName(firstName);
		client.setLastName(lastName);
		client.setEmail(email);
		client.setTel(tel);
		client.setTelfixe(telfixe);
		client.setAdresse(adresse);
		client.setDateCreation(DateCreation);
		client.setPassword(password);
		client.setCarteNational(CarteNational);

		clientRepository.save(client);
	}

	/**
	 * Trouve un client par son email
	 */
	public Client findByEmail(String email) {
		return clientRepository.findByEmail(email);
	}

	@Override
	public List<Client> findAll() {
		return clientRepository.findAll();
	}
	public Page<Client> findByAvocat(Avocat avocat, Pageable pageable) {
		return clientRepository.findByAvocats(avocat, pageable);
	}


	public List<Client> findAllByAvocat(Avocat avocat) {
		// Utilisez un Pageable qui récupère tous les clients en une seule page
		Pageable unpaged = Pageable.unpaged();
		return findByAvocat(avocat, unpaged).getContent();
	}
	public long countByAvocat(Avocat avocat) {
		return clientRepository.countByAvocats(avocat);
	}

	@Override
	public Client findById(Long id) {
		return clientRepository.findById(id).orElse(null);
	}

	/**
	 * Version améliorée qui retourne un Optional
	 * À utiliser si l'interface ClientServiceInterface le permet
	 */
	public Optional<Client> findByIdOptional(Long id) {
		return clientRepository.findById(id);
	}

	@Override
	public List<Client> findByfirstName(String cl) {
		return clientRepository.findByfirstNameContaining(cl);
	}

	@Override
	public List<Dossier> findAllDossier() {
		return dossierRepository.findAll();
	}
}