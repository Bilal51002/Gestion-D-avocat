package org.baeldung.persistence.dao.pfe;

import org.baeldung.persistence.model.pfe.Juge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JugeRepository extends JpaRepository<Juge, Long> {
}
