package org.baeldung.persistence.model.pfe;

import lombok.*;
import org.baeldung.persistence.model.User;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@DiscriminatorValue("CLIENT")
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class Client extends User {

	@ManyToOne
	@JoinColumn(name = "id_avocat_principal")
	private Avocat avocatPrincipal;


	@ToString.Exclude
	@ManyToMany(mappedBy = "clients")
	private Collection<BureauAvocat> bureauAvocats;

	@ToString.Exclude
	@ManyToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
	private Collection<Avocat> avocats;  // Renamed from avocat to avocats for consistency

	@ToString.Exclude
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable(name = "dossier_Client",
			joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "dossier_id", referencedColumnName = "id"))
	private Collection<Dossier> dossiers;  // Renamed from dossier to dossiers for consistency

	@ToString.Exclude
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RDV> rendezvous;

	// Builder pattern for a class that inherits
	@Builder
	public Client(Long id, String firstName, String lastName, String email, String password,
				  String tel, String telfixe, String adresse, java.sql.Date dateCreation,
				  String carteNational, String imguser, Boolean enabled, boolean isUsing2FA,
				  String secret, Collection<org.baeldung.persistence.model.Role> roles,
				  Collection<BureauAvocat> bureauAvocats, Collection<Avocat> avocats,
				  Collection<Dossier> dossiers, List<RDV> rendezvous) {
		super(id, firstName, lastName, email, tel, telfixe, adresse, dateCreation, carteNational,
				imguser, password, enabled != null ? enabled : false, isUsing2FA, secret, roles);
		this.bureauAvocats = bureauAvocats;
		this.avocats = avocats;  // Renamed from avocat to avocats
		this.dossiers = dossiers;  // Renamed from dossier to dossiers
		this.rendezvous = rendezvous;
	}
}