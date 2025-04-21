package org.baeldung.persistence.model.pfe;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import lombok.Data;
import org.baeldung.persistence.model.User;

@Entity
//@Data
public class Secretaire extends User  implements Serializable{

	//private String imguser;
	/*
	 * @Id
	 * 
	 * @Column(unique = true, nullable = false)
	 * 
	 * @GeneratedValue(strategy = GenerationType.AUTO) private Long id;
	 * 
	 * 
	 * private String firstName; private String lastName; private String email;
	 * private String tel; private String telfixe; private String adresse; private
	 * Date DateCreation; private String CarteNational;
	 * 
	 * @Column(length = 60) private String password;
	 * 
	 * 
	 * public String getPassword() { return password; }
	 * 
	 * public void setPassword(String password) { this.password = password; }
	 * 
	 * @Lob
	 * 
	 * @Column(columnDefinition="MEDIUMBLOB") private String imguser;
	 * 
	 * 
	 * public Long getId() { return id; }
	 * 
	 * public void setId(Long id) { this.id = id; }
	 * 
	 * public String getFirstName() { return firstName; }
	 * 
	 * public void setFirstName(String firstName) { this.firstName = firstName; }
	 * 
	 * public String getLastName() { return lastName; }
	 * 
	 * public void setLastName(String lastName) { this.lastName = lastName; }
	 * 
	 * public String getEmail() { return email; }
	 * 
	 * public void setEmail(String email) { this.email = email; }
	 * 
	 * public String getTel() { return tel; }
	 * 
	 * public void setTel(String tel) { this.tel = tel; }
	 * 
	 * public String getTelfixe() { return telfixe; }
	 * 
	 * public void setTelfixe(String telfixe) { this.telfixe = telfixe; }
	 * 
	 * public String getAdresse() { return adresse; }
	 * 
	 * public void setAdresse(String adresse) { this.adresse = adresse; }
	 * 
	 * public Date getDateCreation() { return DateCreation; }
	 * 
	 * public void setDateCreation(Date dateCreation) { DateCreation = dateCreation;
	 * }
	 * 
	 * public String getCarteNational() { return CarteNational; }
	 * 
	 * public void setCarteNational(String carteNational) { CarteNational =
	 * carteNational; }
	 * 
	 * public String getImguser() { return imguser; }
	 * 
	 * public void setImguser(String imguser) { this.imguser = imguser; }
	 * 
	 * @Override public String toString() { return "Secretaire [id=" + id +
	 * ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email +
	 * ", tel=" + tel + ", telfixe=" + telfixe + ", adresse=" + adresse +
	 * ", DateCreation=" + DateCreation + ", CarteNational=" + CarteNational +
	 * ", imguser=" + imguser + ", getId()=" + getId() + ", getFirstName()=" +
	 * getFirstName() + ", getLastName()=" + getLastName() + ", getEmail()=" +
	 * getEmail() + ", getTel()=" + getTel() + ", getTelfixe()=" + getTelfixe() +
	 * ", getAdresse()=" + getAdresse() + ", getDateCreation()=" + getDateCreation()
	 * + ", getCarteNational()=" + getCarteNational() + ", getImguser()=" +
	 * getImguser() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() +
	 * ", toString()=" + super.toString() + "]"; }
	 * 
	 * public Secretaire(Long id, String firstName, String lastName, String email,
	 * String tel, String telfixe, String adresse, Date dateCreation, String
	 * carteNational, String imguser) { super(); this.id = id; this.firstName =
	 * firstName; this.lastName = lastName; this.email = email; this.tel = tel;
	 * this.telfixe = telfixe; this.adresse = adresse; DateCreation = dateCreation;
	 * CarteNational = carteNational; this.imguser = imguser; }
	 * 
	 * public Secretaire() { super(); // TODO Auto-generated constructor stub }
	 * 
	 */

}
