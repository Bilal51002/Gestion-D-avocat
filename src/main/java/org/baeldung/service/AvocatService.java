package org.baeldung.service;

import java.io.IOException;
import java.sql.Date;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.baeldung.persistence.dao.pfe.*;
import org.baeldung.persistence.model.pfe.*;
import org.baeldung.service.pfe.AvocatServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;


@Service
public class AvocatService implements AvocatServiceInterface {
	@Autowired
	private AvocatRepository avocatRepository;
	@Autowired
	private BureauAvocatRepository bureauAvocatRepository;
	@Autowired
	private BarreauRepository barreauRepository;
	@Autowired
	private VilleRepository villeRepository;
	@Autowired
	private ClientRepository clientRepository;


	public boolean verifierAppartenanceBureau(Long avocatId, Long bureauId) {
		// Vérifier si l'avocat existe
		Avocat avocat = avocatRepository.findById(avocatId)
				.orElseThrow(() -> new EntityNotFoundException("Avocat non trouvé"));

		// Vérifier si le bureau existe
		BureauAvocat bureau = bureauAvocatRepository.findById(bureauId)
				.orElseThrow(() -> new EntityNotFoundException("Bureau non trouvé"));

		// Vérifier si l'avocat appartient au bureau
		return avocat.getBureau() != null && avocat.getBureau().getId().equals(bureauId);
	}
	public Collection<Avocat> findAllById(List<Long> ids) {
		return avocatRepository.findAllById(ids);
	}
	public Avocat findByEmail(String email) {
		// Ici nous supposons que votre AvocatRepository a une méthode findByEmail
		// Si ce n'est pas le cas, vous devrez l'ajouter également
		return avocatRepository.findByEmail(email);
	}

	  @Override
	    public void saveAvocattoDB(MultipartFile file,
	    		String firstName,
	    		String lastName,
	    		String email,
	    		String tel,
	    		String telfixe,
	    		String adresse,
	    		Date DateCreation,
	    		String password,
	    		Barreau idBarreau,
	    		String CarteNational) {

	    	    Avocat avocat = new Avocat();
	    	   String fileName=StringUtils.cleanPath(file.getOriginalFilename());
	    	  if(fileName.contains("..")) {
	    		System.out.println("not a valid file");
	    	   }
	    	   try {
				avocat.setImguser(Base64.getEncoder().encodeToString(file.getBytes()));
			   } catch (IOException e) {

				e.printStackTrace();
			}
	    	avocat.setFirstName(firstName);
	    	avocat.setLastName(lastName);
	    	avocat.setEmail(email);
	    	avocat.setTel(tel);
	    	avocat.setTelfixe(telfixe);
	    	avocat.setAdresse(adresse);
	    	avocat.setDateCreation(DateCreation);
	    	avocat.setPassword(password);

	    	System.err.println(idBarreau.getNomBarreau());
	    	avocat.setBarreau(idBarreau);
	    	avocat.setCarteNational(CarteNational);
	        avocatRepository.save(avocat);
	      }

	//return liste of avocat
	@Override
	public List<Avocat> findAll() {
		return avocatRepository.findAll();
	}

	@Override
	public Avocat findById(Long id) {
		return avocatRepository.findById(id).get();
	}

	@Override
	public List<Avocat> findByfirstName(String mt) {

		return avocatRepository.findByfirstNameContaining(mt);
	}
	@Override
	public List<Barreau> findBynomBarreau(String mv) {
		// TODO Auto-generated method stub
		return barreauRepository.findBynomBarreauContaining(mv);
	}
	public Barreau findBarreauById(Long id) {
		return barreauRepository.findById(id).orElse(null);
	}



	@Override
	public List<Barreau> findAllBarreaux() {
		return barreauRepository.findAll();
	}
	@Override
	public List<Client> findAllClient() {
		return clientRepository.findAll();
	}

	@Override
	public List<Ville> findAllVilles() {
		return villeRepository.findAll();
	}

	@Override
	public void deleteAvocat(Long id) {
		  avocatRepository.deleteById(id);

	}


	@Override
	public Avocat addAvocat(Avocat avocatAj) {
		// TODO Auto-generated method stub
		return null;
	}

}