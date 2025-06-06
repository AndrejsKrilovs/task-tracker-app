package krilovs.andrejs.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import krilovs.andrejs.app.entity.UserRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "User registration request. Request to register in our system")
public record UserRegistrationRequest(
  @NotNull(message = "Username is required")
  @NotBlank(message = "Username is required")
  @Schema(
    minLength = 4,
    maxLength = 30,
    required = true,
    title = "Username",
    defaultValue = "username",
    description = "Represents user login"
  )
  String username,

  @NotNull(message = "Password is required")
  @NotBlank(message = "Password is required")
  @Schema(description = "Password", defaultValue = "StrongPass123")
  String password,

  @Email(message = "Invalid email format")
  @Size(min = 3, max = 10, message = "Password must be between 3 and 10 characters")
  @Schema(
    minLength = 3,
    maxLength = 10,
    title = "Email",
    defaultValue = "some@test.email",
    description = "Represents user email"
  )
  String email,

  @Schema(
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
  UserRole role
) {
}
