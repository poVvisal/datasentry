package dev.datasentry.datasets.web;

import dev.datasentry.datasets.DatasetService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for dataset metadata.
 *
 * <p>Routes:
 *
 * <ul>
 *   <li>POST /api/v1/datasets – create dataset metadata record
 *   <li>GET /api/v1/datasets – list all datasets (newest first)
 *   <li>GET /api/v1/datasets/{id} – fetch one by UUID
 * </ul>
 *
 * <p>Validation errors and not-found cases are handled by {@link
 * dev.datasentry.shared.error.GlobalExceptionHandler}.
 */
@RestController
@RequestMapping("/api/v1/datasets")
@RequiredArgsConstructor
public class DatasetController {

  private final DatasetService datasetService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DatasetResponse create(@Valid @RequestBody CreateDatasetRequest request) {
    return datasetService.create(request);
  }

  @GetMapping
  public List<DatasetResponse> list() {
    return datasetService.listAll();
  }

  @GetMapping("/{id}")
  public DatasetResponse get(@PathVariable UUID id) {
    return datasetService.findById(id);
  }
}
