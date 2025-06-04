package krilovs.andrejs.app.dto;

import krilovs.andrejs.app.entity.UserRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserResponse(
  @Schema(description = "Username", defaultValue = "username")
  String username,

  @Schema(description = "Email", defaultValue = "test@example.com")
  String email,

  @Schema(description = "User role", defaultValue = "SOFTWARE_DEVELOPER")
  UserRole role,

  @Schema(description = "Date when user is created")
  LocalDateTime createdAt,

  @Schema(description = "Date when user last time connected")
  LocalDateTime lastVisitAt
) {

}
