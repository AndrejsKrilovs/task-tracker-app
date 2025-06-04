package krilovs.andrejs.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import krilovs.andrejs.app.entity.UserRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record UserRegistrationRequest(
  @NotNull(message = "Username is required")
  @NotBlank(message = "Username is required")
  @Schema(description = "Username", defaultValue = "username")
  @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
  String username,

  @NotNull(message = "Password is required")
  @NotBlank(message = "Password is required")
  @Schema(description = "Password", defaultValue = "StrongPass123")
  String password,

  @Email(message = "Invalid email format")
  @Schema(description = "Email", defaultValue = "test@example.com")
  String email,

  @Schema(description = "User role")
  UserRole role
) {
}
