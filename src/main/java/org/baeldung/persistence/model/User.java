package org.baeldung.persistence.model;

import java.sql.Date;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;
import org.jboss.aerogear.security.otp.api.Base32;
import javax.persistence.Id;
@Entity
@Table(name = "user_account")
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class User {

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
	@Lob
	@Column(columnDefinition="MEDIUMBLOB")
	private String imguser;

	@Column(length = 60)
    private String password;
    private boolean enabled;
    private boolean isUsing2FA;
    private String secret;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User() {
        super();
        this.secret = Base32.random();
        this.enabled = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((email == null) ? 0 : email.hashCode());
        return result;
    }



    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User user = (User) obj;
        if (!email.equals(user.email)) {
            return false;
        }
        return true;
    }

	public User(Long id, String firstName, String lastName, String email, String tel, String telfixe, String adresse,
			Date dateCreation, String carteNational, String imguser, String password, boolean enabled,
			boolean isUsing2FA, String secret, Collection<Role> roles) {
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
		this.imguser = imguser;
		this.password = password;
		this.enabled = enabled;
		this.isUsing2FA = isUsing2FA;
		this.secret = secret;
		this.roles = roles;
	}


}