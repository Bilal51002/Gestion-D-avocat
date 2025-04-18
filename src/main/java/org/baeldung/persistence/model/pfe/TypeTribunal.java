package org.baeldung.persistence.model.pfe;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class TypeTribunal {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String type;

}
