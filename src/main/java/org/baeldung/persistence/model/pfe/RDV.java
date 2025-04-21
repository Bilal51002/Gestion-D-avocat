package org.baeldung.persistence.model.pfe;

import lombok.Data;
import java.util.Date;
import javax.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
public class RDV {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	//private Date date;
	private String heur;
	private String sujet;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;  // Changer le type de Date à LocalDate
	@ManyToOne @JoinColumn(name = "id_client", nullable = false)
	private Client client;
	
	@ManyToOne @JoinColumn(name = "id_bureau", nullable = false)
	private BureauAvocat bureau;
	

}
