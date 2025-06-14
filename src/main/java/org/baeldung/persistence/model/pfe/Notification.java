package org.baeldung.persistence.model.pfe;

import org.baeldung.persistence.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "avocat_id")
    private Avocat avocat;  // Ajout de cette propriété manquante

    @ManyToOne
    @JoinColumn(name = "rdv_id")
    private RDV rendezVous;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User destinataire;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(nullable = false)
    private boolean lu = false;

    @Column(nullable = false)
    private String type; // RDV_DEMANDE, RDV_ACCEPTE, RDV_REFUSE, etc.

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RDV getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(RDV rendezVous) {
        this.rendezVous = rendezVous;
    }

    public User getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(User destinataire) {
        this.destinataire = destinataire;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
