package org.baeldung.persistence.model.pfe;

import java.io.Serializable;
import javax.persistence.*;
import org.baeldung.persistence.model.User;

@Entity
@DiscriminatorValue("SECRETAIRE")
public class Secretaire extends User implements Serializable {
    @OneToOne(mappedBy = "secretaire")
    private BureauAvocat bureau;

    // Getters and setters
    public BureauAvocat getBureau() {
        return bureau;
    }

    public void setBureau(BureauAvocat bureau) {
        this.bureau = bureau;
    }
}
