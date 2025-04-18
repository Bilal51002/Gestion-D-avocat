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

import lombok.Data;
import org.baeldung.persistence.model.User;



@Entity
@Data
public class Avocat extends User implements Serializable{

		
		
		//@Column(name = "Haya_AVOCATS")
		//private Long idBarreau;
		
		@ManyToOne(cascade = CascadeType.REMOVE)
		@JoinColumn(name = "Haya_AVOCAT",referencedColumnName="ID_BARREAU")
		private Barreau barreau;
		private String image;
		
		private boolean fonctionalite;
		 @ManyToMany(mappedBy = "avocat",cascade = CascadeType.REMOVE)
		 private Collection<Dossier> dossier;
		 
		 /*@ManyToOne(cascade = CascadeType.REMOVE) 
			@JoinColumn(name = "id_bureau", nullable = false)
			private BureauAvocat bureau;*/
		public Collection<Dossier> getDossier() {
			return dossier;
		}
		public void setDossier(Collection<Dossier> dossier) {
			this.dossier = dossier;
		}
		@ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
		@JoinTable( name = "client_Avocat", joinColumns = @JoinColumn( name = "avocat_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn( name = "client_id", referencedColumnName = "id"))
		private Collection<Client> client;
		
		/*
		 * public Collection<Client> getClient() { return client; } public void
		 * setClient(Collection<Client> client) { this.client = client; }
		 */
		
 		
		/*
		 * public Collection<Dossier> getDossier() { return dossier; }
		 */
		public Collection<Client> getClient() {
			return client;
		}
		public void setClient(Collection<Client> client) {
			this.client = client;
		}

		/*
		 * public void setDossier(Collection<Dossier> dossier) { this.dossier = dossier;
		 * }
		 */
	//	public Long getIdBarreau() {
		//	return idBarreau;
	//	}
		//public void setIdBarreau(Long idBarreau) {
		//	this.idBarreau = idBarreau;
		//}
		public Barreau getBarreau() {
			return barreau;
		}
		public void setBarreau(Barreau barreau) {
			this.barreau = barreau;
		}
		public boolean isFonctionalite() {
			return fonctionalite;
		}
		public void setFonctionalite(boolean fonctionalite) {
			this.fonctionalite = fonctionalite;
		}
		
		
		/*
		 * public BureauAvocat getBureau() { return bureau; } public void
		 * setBureau(BureauAvocat bureau) { this.bureau = bureau; }
		 */
		
		public Avocat(boolean fonctionalite, Long idBarreau, Barreau barreau,
				Collection<Dossier> dossier, Collection<Client> client) {
			super();
			this.fonctionalite = fonctionalite;
			//this.idBarreau = idBarreau;

			this.barreau = barreau;
			this.dossier = dossier;
			this.client = client;
		}
		@Override
		public String toString() {
			return "Avocat [fonctionalite=" + fonctionalite + ", barreau=" + barreau
					+ ", dossier=" + dossier + ", client=" + client + ", getDossier()=" + getDossier()
					+ ", getClient()=" + getClient() + ", getBarreau()="
					+ getBarreau() + ", isFonctionalite()=" + isFonctionalite() + ", getDateCreation()="
					+ getDateCreation() + ", getCarteNational()=" + getCarteNational() + ", getTelfixe()="
					+ getTelfixe() + ", getAdresse()=" + getAdresse() + ", getImguser()=" + getImguser() + ", getId()="
					+ getId() + ", getFirstName()=" + getFirstName() + ", getLastName()=" + getLastName()
					+ ", getEmail()=" + getEmail() + ", getTel()=" + getTel() + ", getPassword()=" + getPassword()
					+ ", getRoles()=" + getRoles() + ", isEnabled()=" + isEnabled() + ", isUsing2FA()=" + isUsing2FA()
					+ ", getSecret()=" + getSecret() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ ", getClass()=" + getClass() + "]";
		}
		public Avocat() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		 
		
			/*
			 * public char[] getfirstName() { // TODO Auto-generated method stub return
			 * null; }
			 */
		 
	

}
