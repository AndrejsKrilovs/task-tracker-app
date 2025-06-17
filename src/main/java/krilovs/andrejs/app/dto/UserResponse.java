package krilovs.andrejs.app.dto;

import krilovs.andrejs.app.entity.UserRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "User response. User response for register in our system")
public record UserResponse(
  @Schema(
    readOnly = true,
    title = "Username",
    description = "Represents user login"
  )
  String username,

  @Schema(
    readOnly = true,
    title = "Email",
    description = "Represents user email"
  )
  String email,

  @Schema(
    readOnly = true,
    title = "User role",
    anyOf = UserRole.class,
    description = "Shows registered user role",
    enumeration = {
      "PRODUCT_OWNER",
      "BUSINESS_ANALYST",
      "SCRUM_MASTER",
      "SOFTWARE_DEVELOPER",
      "QA_SPECIALIST"
    }
  )
  UserRole role,

  @Schema(
    readOnly = true,
    title = "Date when user is created",
    description = "Date when user is created"
  )
  LocalDateTime createdAt,

  @Schema(
    readOnly = true,
    title = "Date when user last logged in",
    description = "Date when user last logged in"
  )
  LocalDateTime lastVisitAt,

  @Schema(
    readOnly = true,
    title = "User permissions",
    description = "Shows what user allow in out system"
  )
  List<UserPermissions> userPermissions,

  @Schema(
    readOnly = true,
    title = "Generated token",
    description = "Remove this field after frontend logic refactor"
  )
  String token
) {
}
