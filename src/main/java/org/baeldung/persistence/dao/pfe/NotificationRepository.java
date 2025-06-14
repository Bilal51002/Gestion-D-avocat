package org.baeldung.persistence.dao.pfe;

import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<org.baeldung.persistence.model.pfe.Notification> findByDestinataireAndLuOrderByDateCreationDesc(org.baeldung.persistence.model.User destinataire, boolean lu);
    List<org.baeldung.persistence.model.pfe.Notification> findByDestinataireOrderByDateCreationDesc(org.baeldung.persistence.model.User destinataire);
    long countByDestinataireAndLu(org.baeldung.persistence.model.User destinataire, boolean lu);
}

