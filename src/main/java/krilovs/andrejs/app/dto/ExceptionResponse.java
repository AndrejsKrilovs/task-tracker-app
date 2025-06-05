package krilovs.andrejs.app.dto;

import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Error response for failed API requests")
public record ExceptionResponse(
  @Schema(description = "Error status", examples = {"BAD_REQUEST", "CONFLICT", "UNAUTHORIZED"})
  Response.Status status,
  LocalDateTime timestamp,
  String path,

  @Schema(description = "Message or messages response", anyOf = {String.class, Map.class})
  Object message
) {
}
