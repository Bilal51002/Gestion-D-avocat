package org.baeldung.persistence.model.pfe;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity

public class TribunalFils {
	
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Long id;
	private String nom;
	@ManyToOne(cascade = CascadeType.ALL) @JoinColumn(name = "id_mere", nullable = false)
	private TribunalMere mere;

	public TribunalMere getMere() {
		return mere;
	}
	public void setMere(TribunalMere mere) {
		this.mere = mere;
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
	
	

}
