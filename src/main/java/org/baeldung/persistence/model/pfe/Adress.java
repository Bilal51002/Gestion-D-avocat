package org.baeldung.persistence.model.pfe;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Data
public class Adress {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Long id;
	private String nom;
	private boolean supp;
	
	@OneToOne @JoinColumn(name = "id_bureau", nullable = false)
	private BureauAvocat bureau;

	@ManyToOne
	@JoinColumn(name = "id_ville", nullable = false)
	private Ville ville;

}
