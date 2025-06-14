package org.baeldung.persistence.model.pfe;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import lombok.*;
import org.baeldung.persistence.model.User;
@Entity
@Getter
@Setter
@ToString(exclude = {"dossier", "client"})
@DiscriminatorValue("AVOCAT")
public class Avocat extends User implements Serializable{
		@ManyToOne(cascade = CascadeType.REMOVE)
		@JoinColumn(name = "Haya_AVOCAT",referencedColumnName="ID_BARREAU")
		private Barreau barreau;
//		 @ManyToMany( fetch = FetchType.LAZY, mappedBy = "avocat")
//		 private Collection<Dossier> dossier;
@ManyToMany(mappedBy = "avocat")
private Set<Dossier> dossier = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "bureau_id")
	private BureauAvocat bureau;

	// Getter and setter for bureau
	public BureauAvocat getBureau() {
		return bureau;
	}

	public void setBureau(BureauAvocat bureau) {
		this.bureau = bureau;
	}
		@ManyToMany(cascade = CascadeType.REMOVE)
		@JoinTable( name = "client_Avocat", joinColumns = @JoinColumn( name = "avocat_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn( name = "client_id", referencedColumnName = "id"))
		private Collection<Client> client;

		public Avocat() {
			super();
		}
}
