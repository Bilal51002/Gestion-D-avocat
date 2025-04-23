package org.baeldung.persistence.model.pfe;

import lombok.*;
import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

	@Id
	@Column(unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String firstName;
	private String lastName;
	private String email;
	private String tel;
	private String telfixe;
	private String adresse;
	private Date DateCreation;
	private String CarteNational;

	@Column(length = 60)
	private String password;

	@Lob
	@Column(columnDefinition="MEDIUMBLOB")
	private String imguser;

	private String image;
	@ManyToMany(mappedBy = "clients")
	private Collection<BureauAvocat> bureauAvocats;

	@ToString.Exclude
	@ManyToMany(mappedBy = "clients", cascade = CascadeType.REMOVE)
	private Collection<BureauAvocat> bureau;

	@ToString.Exclude
	@ManyToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
	private Collection<Avocat> avocat;

	@ToString.Exclude
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "dossier_Client",
			joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "dossier_id", referencedColumnName = "id"))
	private Collection<Dossier> dossier;

	@ToString.Exclude
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RDV> rendezvous;

}
