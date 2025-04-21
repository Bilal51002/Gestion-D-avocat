package org.baeldung.persistence.model.pfe;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CalendrieSeance {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
	
	private Long id;
    private String  natureSeance;
    private Date DateSeance;
    private String commentaire;
    private Time heure;
    private String  salle;
    @ManyToOne
	@JoinColumn(name = "Type_filier",referencedColumnName="ID_FILIER", insertable=false, updatable=false)
	private Filier filier;
    @ManyToOne
    @JoinColumn(name = "dossier_Seance",referencedColumnName="NUMERO_DOSSIER", insertable=false, updatable=false)
    private Dossier dossiers;
    
    
	public String getCommentaire() {
		return commentaire;
	}
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}
	public Date getDateSeance() {
		return DateSeance;
	}
	public void setDateSeance(Date dateSeance) {
		DateSeance = dateSeance;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNatureSeance() {
		return natureSeance;
	}
	public void setNatureSeance(String natureSeance) {
		this.natureSeance = natureSeance;
	}
	
	
	public Time getHeure() {
		return heure;
	}
	public void setHeure(Time heure) {
		this.heure = heure;
	}
	public String getSalle() {
		return salle;
	}
	public void setSalle(String salle) {
		this.salle = salle;
	}
	public Filier getFilier() {
		return filier;
	}
	public void setFilier(Filier filier) {
		this.filier = filier;
	}
	public Dossier getDossiers() {
		return dossiers;
	}
	public void setDossiers(Dossier dossiers) {
		this.dossiers = dossiers;
	}
    
    
}
