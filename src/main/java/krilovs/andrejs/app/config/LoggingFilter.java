package krilovs.andrejs.app.config;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

@Slf4j
@Provider
@Priority(1)
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String requestId = UUID.randomUUID().toString();

    MDC.put("requestId", requestId);
    MDC.put("method", requestContext.getMethod());
    MDC.put("path", requestContext.getUriInfo().getPath());

    log.info("Incoming request: [{} {}], Headers: {}",
      requestContext.getMethod(),
      requestContext.getUriInfo().getPath(),
      requestContext.getHeaders()
    );
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    log.info("Outgoing response: status={}, headers={}",
      responseContext.getStatus(),
      responseContext.getHeaders()
    );

    MDC.clear();
  }
}
