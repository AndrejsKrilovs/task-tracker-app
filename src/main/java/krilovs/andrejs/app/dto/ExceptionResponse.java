package krilovs.andrejs.app.dto;

import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Error response for failed API requests")
public record ExceptionResponse(
  @Schema(
    readOnly = true,
    title = "Error status",
    description = "Error status",
    enumeration = {
      "BAD_REQUEST",
      "CONFLICT",
      "UNAUTHORIZED"
    }
  )
  Response.Status status,

  @Schema(
    readOnly = true,
    title = "Timestamp",
    description = "Shows date when exception was thrown"
  )
  LocalDateTime timestamp,

  @Schema(
    readOnly = true,
    title = "Path to endpoint when was an error",
    description = "Shows date when exception was thrown"
  )
  String path,

  @Schema(
    readOnly = true,
    title = "Error message/messages with incorrect property name",
    description = "Show message or messages response. Can be string or Map"
  )
  Object message
) {
}
