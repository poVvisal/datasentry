package dev.datasentry.shared.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Extracts or generates a correlation ID for every request. Stores it in MDC so all log lines and
 * error responses carry the same ID.
 *
 * <p>Downstream systems may pass {@code X-Correlation-Id}; if absent, a new UUID is generated.
 */
@Component
@Order(1)
@Slf4j
public class CorrelationIdFilter extends OncePerRequestFilter {

  private static final String HEADER = "X-Correlation-Id";
  private static final String MDC_KEY = "correlationId";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String correlationId = request.getHeader(HEADER);
    if (correlationId == null || correlationId.isBlank()) {
      correlationId = UUID.randomUUID().toString();
    }

    MDC.put(MDC_KEY, correlationId);
    response.setHeader(HEADER, correlationId);

    try {
      chain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }
}
