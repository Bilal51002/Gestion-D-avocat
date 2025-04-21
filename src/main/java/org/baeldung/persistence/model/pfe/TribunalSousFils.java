package org.baeldung.persistence.model.pfe;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity

public class TribunalSousFils {
	
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Long id;
	private String nom;
	@ManyToOne(cascade = CascadeType.ALL) @JoinColumn(name = "id_fils", nullable = false)
	private TribunalFils fils;

	public TribunalFils getFils() {
		return fils;
	}
	public void setFils(TribunalFils fils) {
		this.fils = fils;
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
