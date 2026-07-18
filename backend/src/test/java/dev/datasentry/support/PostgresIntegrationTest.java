package dev.datasentry.support;

import java.lang.annotation.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Meta-annotation that spins up a real PostgreSQL container via Testcontainers and wires it into
 * the Spring Boot datasource automatically.
 *
 * <p>Usage: annotate any integration test class with {@code @PostgresIntegrationTest}.
 *
 * <p><b>Requires Docker to be running locally.</b> If Docker is unavailable, see README § Local
 * PostgreSQL fallback.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface PostgresIntegrationTest {

  @Container
  @ServiceConnection
  @SuppressWarnings("unused")
  PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("datasentry_test")
          .withUsername("test")
          .withPassword("test");
}
