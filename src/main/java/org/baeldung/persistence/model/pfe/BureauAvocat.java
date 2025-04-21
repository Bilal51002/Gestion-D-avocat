package org.baeldung.persistence.model.pfe;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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
	@Lob
	@Column(columnDefinition="MEDIUMBLOB")
	private String image;
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
	  ", adresse=" + adresse + ", Supp=" + Supp + ", image=" + image + ", clients="
	  + clients + "]"; }

}
