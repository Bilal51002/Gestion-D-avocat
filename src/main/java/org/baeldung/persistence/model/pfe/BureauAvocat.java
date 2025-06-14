package org.baeldung.persistence.model.pfe;

import java.util.Collection;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Data
@Entity
public class BureauAvocat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nom;
	private String email;
	private String tel;
	private String telfex;
	private String adresse;
	private String maps;

	@ManyToOne
	private Avocat avocat;

	// Getter
	public Avocat getAvocat() {
		return avocat;
	}

	// Setter
	public void setAvocat(Avocat avocat) {
		this.avocat = avocat;
	}
	@Lob
	@Column(columnDefinition="MEDIUMBLOB")
	private String image;
	private String secret;
	@Column(nullable = false)
	private Boolean enabled = true; // or false, depending on your requirement
	private boolean isUsing2FA;
	@OneToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "secretaire_id", unique = true, referencedColumnName = "id")
	private Secretaire secretaire;


	private int Supp;
	private String password;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private Collection<Client> clients;

	@OneToMany(mappedBy = "bureau", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private Collection<Dossier> dossier;

	@Override
	public String toString() {
		return "BureauAvocat [id=" + id +", nom=" + nom + ", email=" + email + ", tel=" + tel + ", telfex=" + telfex +
				", adresse=" + adresse + ", Supp=" + Supp + ", image=" + image + ", secretaire=" +
				(secretaire != null ? secretaire.getId() : "null") + ", clients=" + clients + "]";
	}
}