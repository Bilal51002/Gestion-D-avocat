package org.baeldung.persistence.model.pfe;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity

public class TribunalMere {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Long id;
	
	private String type;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	

}
