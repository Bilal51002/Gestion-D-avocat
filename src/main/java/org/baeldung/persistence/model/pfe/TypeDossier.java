package org.baeldung.persistence.model.pfe;

import lombok.Data;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
@Entity
@Data
public class TypeDossier {
@Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
@Column(unique = true, nullable = false, name = "ID_TYPE")
	private Long id;
    private String nom;
    @OneToMany(mappedBy = "typeD")
	private Collection<Dossier> dossier;
    
	public TypeDossier() {
		super();
		
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
	public Collection<Dossier> getDossier() {
		return dossier;
	}
	public void setDossier(Collection<Dossier> dossier) {
		this.dossier = dossier;
	}
    
}
