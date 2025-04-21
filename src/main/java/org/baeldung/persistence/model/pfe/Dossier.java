package org.baeldung.persistence.model.pfe;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Dossier implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@Column(unique = true, nullable = false, length = 20, name = "NUMERO_DOSSIER")
	private String numeroDossier;

	private Date DateCreation;
	private String typeDecas;
	private String sujet;
	private String typeProsedure;
	private Date dateProchSession;
	private String id_client;

	@Column(unique = true, length = 20, name = "NUMERO_NATIONAL")
	private String numeroNational;

	@ToString.Exclude
	@OneToMany(mappedBy = "dossiers", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Collection<CalendrieSeance> calendrieSeances;

	@ToString.Exclude
	@ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_bureau", nullable = false)
	private BureauAvocat bureau;

	@ToString.Exclude
	@ManyToMany(mappedBy = "dossier", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	private Collection<Client> client;

	@ToString.Exclude
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "dossier_juge",
			joinColumns = @JoinColumn(name = "dossier_id"),
			inverseJoinColumns = @JoinColumn(name = "juge_id"))
	private Collection<Juge> juge;

	@ToString.Exclude
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "dossie_tri",
			joinColumns = @JoinColumn(name = "dossier_id"),
			inverseJoinColumns = @JoinColumn(name = "tri_id"))
	private Collection<Tribunal> tribunal;

	@ToString.Exclude
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "Type_Dossier", referencedColumnName = "ID_TYPE")
	private TypeDossier typeD;

	@ToString.Exclude
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "dossier_Avocat",
			joinColumns = @JoinColumn(name = "dossier_id"),
			inverseJoinColumns = @JoinColumn(name = "avocat_id"))
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
