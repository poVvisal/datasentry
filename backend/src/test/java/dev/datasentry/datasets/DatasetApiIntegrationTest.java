package dev.datasentry.datasets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.datasentry.support.PostgresIntegrationTest;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for POST/GET /api/v1/datasets.
 *
 * <p>Uses a real PostgreSQL container via {@link PostgresIntegrationTest}. Flyway migrations run on
 * context startup, proving schema and migration correctness end-to-end.
 */
@SpringBootTest
@AutoConfigureMockMvc
@PostgresIntegrationTest
class DatasetApiIntegrationTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @Autowired DatasetRepository datasetRepository;

  @BeforeEach
  void cleanDb() {
    datasetRepository.deleteAll();
  }

  // --- Helpers ---

  private Map<String, Object> validRequest() {
    return Map.of(
        "name", "My Test Dataset",
        "originalFilename", "data.csv",
        "contentType", "text/csv",
        "sizeBytes", 1024L,
        "storageKey", "uploads/placeholder-key");
  }

  // --- POST /api/v1/datasets ------------------------------------------------

  @Test
  @DisplayName("POST /api/v1/datasets – 201 with created dataset")
  void createDataset_validRequest_returns201() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/datasets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").value("My Test Dataset"))
        .andExpect(jsonPath("$.status").value("UPLOADED"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty());

    assertThat(datasetRepository.count()).isEqualTo(1);
  }

  @Test
  @DisplayName("POST /api/v1/datasets – 400 when name is blank")
  void createDataset_blankName_returns400() throws Exception {
    var request = new java.util.HashMap<>(validRequest());
    request.put("name", "");

    mockMvc
        .perform(
            post("/api/v1/datasets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.fieldErrors", hasSize(greaterThanOrEqualTo(1))));
  }

  @Test
  @DisplayName("POST /api/v1/datasets – 400 when name is too short (< 3 chars)")
  void createDataset_nameTooShort_returns400() throws Exception {
    var request = new java.util.HashMap<>(validRequest());
    request.put("name", "ab");

    mockMvc
        .perform(
            post("/api/v1/datasets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
  }

  @Test
  @DisplayName("POST /api/v1/datasets – 400 when contentType is not text/csv")
  void createDataset_invalidContentType_returns400() throws Exception {
    var request = new java.util.HashMap<>(validRequest());
    request.put("contentType", "application/json");

    mockMvc
        .perform(
            post("/api/v1/datasets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
  }

  @Test
  @DisplayName("POST /api/v1/datasets – 400 when sizeBytes is zero or negative")
  void createDataset_nonPositiveSize_returns400() throws Exception {
    var request = new java.util.HashMap<>(validRequest());
    request.put("sizeBytes", 0L);

    mockMvc
        .perform(
            post("/api/v1/datasets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
  }

  // --- GET /api/v1/datasets -------------------------------------------------

  @Test
  @DisplayName("GET /api/v1/datasets – 200 with datasets ordered newest-first")
  void listDatasets_returnsNewestFirst() throws Exception {
    // Create two records with slight time separation
    createViaApi("Alpha Dataset");
    Thread.sleep(10); // ensure different createdAt
    createViaApi("Beta Dataset");

    mockMvc
        .perform(get("/api/v1/datasets"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("Beta Dataset"))
        .andExpect(jsonPath("$[1].name").value("Alpha Dataset"));
  }

  // --- GET /api/v1/datasets/{id} -------------------------------------------

  @Test
  @DisplayName("GET /api/v1/datasets/{id} – 200 for existing dataset")
  void getDatasetById_exists_returns200() throws Exception {
    String body = createViaApi("Findable Dataset");
    String id = objectMapper.readTree(body).get("id").asText();

    mockMvc
        .perform(get("/api/v1/datasets/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("Findable Dataset"));
  }

  @Test
  @DisplayName("GET /api/v1/datasets/{id} – 404 for unknown UUID")
  void getDatasetById_notFound_returns404() throws Exception {
    UUID unknownId = UUID.randomUUID();

    mockMvc
        .perform(get("/api/v1/datasets/" + unknownId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("NOT_FOUND"))
        .andExpect(jsonPath("$.path").value("/api/v1/datasets/" + unknownId));
  }

  // --- Helpers --------------------------------------------------------------

  private String createViaApi(String name) throws Exception {
    var request = new java.util.HashMap<>(validRequest());
    request.put("name", name);
    return mockMvc
        .perform(
            post("/api/v1/datasets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }
}
