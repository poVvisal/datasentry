package dev.datasentry.datasets;

import dev.datasentry.datasets.web.CreateDatasetRequest;
import dev.datasentry.datasets.web.DatasetResponse;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates dataset-metadata lifecycle. All DB access goes through {@link DatasetRepository}.
 *
 * <p>The service receives and returns DTOs; it never exposes {@link Dataset} entities to callers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DatasetService {

  private static final int DEFAULT_PAGE_SIZE = 50;

  private final DatasetRepository datasetRepository;

  /**
   * Persists a new dataset metadata record.
   *
   * @param request validated DTO from the web layer
   * @return response DTO with the persisted id and timestamps
   */
  @Transactional
  public DatasetResponse create(CreateDatasetRequest request) {
    Dataset dataset = new Dataset();
    dataset.setName(request.name());
    dataset.setOriginalFilename(request.originalFilename());
    dataset.setContentType(request.contentType());
    dataset.setSizeBytes(request.sizeBytes());
    dataset.setStorageKey(request.storageKey());
    dataset.setStatus(DatasetStatus.UPLOADED);

    Dataset saved = datasetRepository.save(dataset);
    log.debug("Created dataset id={} name={}", saved.getId(), saved.getName());
    return DatasetResponse.from(saved);
  }

  /**
   * Lists all datasets, newest first.
   *
   * @return list of response DTOs ordered by createdAt DESC
   */
  @Transactional(readOnly = true)
  public List<DatasetResponse> listAll() {
    PageRequest page =
        PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
    return datasetRepository.findAll(page).stream().map(DatasetResponse::from).toList();
  }

  /**
   * Fetches a single dataset by its UUID primary key.
   *
   * @param id dataset UUID
   * @return response DTO
   * @throws EntityNotFoundException if no dataset with the given id exists
   */
  @Transactional(readOnly = true)
  public DatasetResponse findById(UUID id) {
    Dataset dataset =
        datasetRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Dataset not found: " + id));
    return DatasetResponse.from(dataset);
  }
}
