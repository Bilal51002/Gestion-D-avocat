package org.baeldung.persistence.model.pfe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Data @NoArgsConstructor
@AllArgsConstructor
public class Dossier implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;
	@Column(unique = true, nullable = false, length = 20, name = "NUMERO_DOSSIER")
	private String numeroDossier;
	private Date DateCreation;
     private String typeDecas;
	 private String sujet;
	 private String typeProsedure;
	 private Date dateProchSession;
	 private String id_client;
	@OneToMany(mappedBy = "dossiers")
	private Collection<CalendrieSeance> calendrieSeances;


	@Column(unique = true, length = 20, name = "NUMERO_NATIONAL")
	private String numeroNational;

     @ManyToOne(cascade = CascadeType.REMOVE)
     @JoinColumn(name = "id_bureau",nullable = false)
	  private BureauAvocat bureau;
	@ManyToMany(mappedBy = "dossier",cascade = CascadeType.MERGE)
	private Collection<Client> client;


	@ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.MERGE)
	@JoinTable( name = "dossier_juge", joinColumns = @JoinColumn( name = "dossier_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn( name = "juge_id", referencedColumnName = "id"))
	private Collection<Juge> juge;

	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.MERGE)
	@JoinTable( name = "dossie_tri", joinColumns = @JoinColumn( name ="dossier_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn( name="tri_id", referencedColumnName = "id"))
	private Collection<Tribunal> tribunal;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "Type_Dossier",referencedColumnName="ID_TYPE")
    private TypeDossier typeD;

	@ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
	@JoinTable( name = "dossier_Avocat", joinColumns = @JoinColumn( name = "dossier_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn( name = "avocat_id", referencedColumnName = "id"))
	private Collection<Avocat> avocat;

	public Dossier( String numeroDossier, Date dateCreation, String typeDecas, String sujet,
			String typeProsedure, Date dateProchSession,
			String numeroNational, TypeDossier typeD,BureauAvocat bureau) {
		this.numeroDossier = numeroDossier;
		this.DateCreation = dateCreation;
		this.typeDecas = typeDecas;
		this.sujet = sujet;
		this.typeProsedure = typeProsedure;
		this.dateProchSession = dateProchSession;
		this.numeroNational = numeroNational;
		this.typeD = typeD;
		this.bureau = bureau;
	}
}
