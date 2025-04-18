package org.baeldung.persistence.model.pfe;



import java.sql.Date;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;

import lombok.Data;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;

@Entity
@Data
public class Client{
	
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
	@ManyToMany(mappedBy ="clients",cascade = CascadeType.REMOVE)
	private Collection<BureauAvocat> bureau;
	
	  @ManyToMany(mappedBy = "client",cascade = CascadeType.REMOVE) 
	  private Collection<Avocat> avocat;
	 
	
	/*@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable( name = "client_avocat", joinColumns = @JoinColumn( name = "client_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn( name = "avocat_id", referencedColumnName = "id"))
	private Collection<Avocat> avocat;*/
	  
	  
	@ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
	@JoinTable( name = "dossier_Client", joinColumns = @JoinColumn( name = "client_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn( name = "dossier_id", referencedColumnName = "id"))
	private Collection<Dossier> dossier;
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RDV> rendezvous;

	public Client(Long id, String firstName, String lastName, String email, String tel, String telfixe, String adresse,
			Date dateCreation, String carteNational, String password, Collection<BureauAvocat> bureau,
			Collection<Avocat> avocat, Collection<Dossier> dossier) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.tel = tel;
		this.telfixe = telfixe;
		this.adresse = adresse;
		DateCreation = dateCreation;
		CarteNational = carteNational;
		this.password = password;
		this.bureau = bureau;
		this.avocat = avocat;
		this.dossier = dossier;
	}
	public Client() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Client(Long id, String firstName, String lastName, String email, String tel, String telfixe, String adresse,
			Date dateCreation, String carteNational, String imguser, String password, boolean enabled,
			boolean isUsing2FA, String secret, Collection<Role> roles) {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	

}
