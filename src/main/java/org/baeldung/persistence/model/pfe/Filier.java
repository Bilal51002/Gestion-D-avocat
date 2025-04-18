package org.baeldung.persistence.model.pfe;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Filier {
	@Id 
	@Column(unique = true, nullable = false, name = "ID_FILIER")
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
	private String nom;
	@OneToMany(mappedBy = "filier", fetch = FetchType.LAZY)
	private Collection<CalendrieSeance> calendrieSeances;
	
	
	
	
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
	public Collection<CalendrieSeance> getCalendrieSCivils() {
		return calendrieSeances;
	}
	public void setCalendrieSCivils(Collection<CalendrieSeance> calendrieSeances) {
		this.calendrieSeances = calendrieSeances;
	}
	
	
	
	
}
