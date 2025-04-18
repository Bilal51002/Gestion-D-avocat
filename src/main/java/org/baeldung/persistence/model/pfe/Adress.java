package org.baeldung.persistence.model.pfe;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity

public class Adress {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Long id;
	private String nom;
	private boolean supp;
	
	@OneToOne @JoinColumn(name = "id_bureau", nullable = false)
	private BureauAvocat bureau;

	@ManyToOne
	@JoinColumn(name = "id_ville", nullable = false)
	private Ville ville;
	
	public BureauAvocat getBureau() {
		return bureau;
	}
	public void setBureau(BureauAvocat bureau) {
		this.bureau = bureau;
	}
	
	public Ville getVille() {
		return ville;
	}
	public void setVille(Ville ville) {
		this.ville = ville;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public boolean isSupp() {
		return supp;
	}
	public void setSupp(boolean supp) {
		this.supp = supp;
	}
	
	

}
