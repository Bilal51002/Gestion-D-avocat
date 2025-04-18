package org.baeldung.persistence.model.pfe;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
@Entity
public class Ville {
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Long id;
	private String nom;
	private boolean supp;
	@OneToMany(mappedBy = "ville")
	private Collection<Adress>  adress;
	
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
