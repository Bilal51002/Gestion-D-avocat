package org.baeldung.persistence.model.pfe;

import java.io.Serializable;
import java.util.Collection;
import java.sql.Date;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.baeldung.persistence.model.User;



@Entity
@Data
@ToString
@NoArgsConstructor
public class Avocat extends User implements Serializable{
		
		@ManyToOne(cascade = CascadeType.REMOVE)
		@JoinColumn(name = "Haya_AVOCAT",referencedColumnName="ID_BARREAU")
		private Barreau barreau;
		private String image;
		
		private boolean fonctionalite;
		 @ManyToMany(mappedBy = "avocat",cascade = CascadeType.REMOVE)
		 private Collection<Dossier> dossier;

		@ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
		@JoinTable( name = "client_Avocat", joinColumns = @JoinColumn( name = "avocat_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn( name = "client_id", referencedColumnName = "id"))
		private Collection<Client> client;
		
		public Avocat(boolean fonctionalite, Long idBarreau, Barreau barreau,
				Collection<Dossier> dossier, Collection<Client> client) {
			super();
			this.fonctionalite = fonctionalite;
			//this.idBarreau = idBarreau;

			this.barreau = barreau;
			this.dossier = dossier;
			this.client = client;
		}
	

}
