package org.baeldung.persistence.model.pfe;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
public class RDV implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Statut statut = Statut.EN_ATTENTE;

	// Getters et setters
	public Statut getStatut() {
		return statut;
	}

	public void setStatut(Statut statut) {
		this.statut = statut;
	}

	private String heur;  // Store the hour as a string
	private String sujet;

	@ManyToOne
	@JoinColumn(name = "avocat_id")
	private Avocat avocat;

	public Avocat getAvocat() {
		return avocat;
	}

	public void setAvocat(Avocat avocat) {
		this.avocat = avocat;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;  // This is just the date with no time component

	@ManyToOne
	@JoinColumn(name = "id_client", nullable = false)
	private Client client;

	private boolean premiereFois;

	@ManyToOne
	@JoinColumn(name = "id_bureau", nullable = false)
	private BureauAvocat bureau;

	public BureauAvocat getBureau() {
		return bureau;
	}

	public void setBureau(BureauAvocat bureau) {
		this.bureau = bureau;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHeur() {
		return heur;
	}

	public void setHeur(String heur) {
		this.heur = heur;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getSujet() {
		return sujet;
	}

	public void setSujet(String sujet) {
		this.sujet = sujet;
	}

}