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

	private String heur;
	private String sujet;

//	@ManyToOne
//	@JoinColumn(name = "id_avocat", nullable = false)
//	private Avocat avocat;


	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@Column(nullable = true)
	private String statut;

	@ManyToOne
	@JoinColumn(name = "id_client", nullable = false)
	private Client client;

	@ManyToOne
	@JoinColumn(name = "id_bureau", nullable = false)
	private BureauAvocat bureau;
}
