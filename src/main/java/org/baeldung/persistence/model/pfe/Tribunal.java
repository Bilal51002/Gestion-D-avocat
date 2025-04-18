package org.baeldung.persistence.model.pfe;

import java.util.Collection;

import javax.persistence.CascadeType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity

public class Tribunal {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nom;
	
	@ManyToOne(cascade = CascadeType.ALL) @JoinColumn(name = "id_type", nullable = false)
	private TypeTribunal type_tri;
	
	@ManyToOne
	@JoinColumn(name="id_tri")
	private Tribunal tribunal;
	
	@ManyToMany(mappedBy = "tribunal")
	private Collection<Dossier> dossier;
	
	
	public Collection<Dossier> getDossier() {
		return dossier;
	}
	public void setDossier(Collection<Dossier> dossier) {
		this.dossier = dossier;
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
	
	public TypeTribunal getType_tri() {
		return type_tri;
	}
	public void setType_tri(TypeTribunal type_tri) {
		this.type_tri = type_tri;
	}
	
	public Tribunal getTribunal() {
		return tribunal;
	}
	public void setTribunal(Tribunal tribunal) {
		this.tribunal = tribunal;
	}

}
