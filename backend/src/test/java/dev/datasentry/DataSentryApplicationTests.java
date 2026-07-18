package dev.datasentry;

import dev.datasentry.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@PostgresIntegrationTest
class DataSentryApplicationTests {

  @Test
  void contextLoads() {
    // Verifies: app context starts, Flyway migrations run, JPA can validate schema
  }
}
