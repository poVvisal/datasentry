package dev.datasentry.datasets;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Spring Data JPA repository for {@link Dataset}.
 *
 * <p>Default ordering (newest first) is applied in the service layer via {@code
 * PageRequest.of(0, N, Sort.by("createdAt").descending())}.
 */
public interface DatasetRepository extends JpaRepository<Dataset, UUID> {

  /** Returns all datasets with caller-supplied sort/page. */
  Page<Dataset> findAll(Pageable pageable);
}
