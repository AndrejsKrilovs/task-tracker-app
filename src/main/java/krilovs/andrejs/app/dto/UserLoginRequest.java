package krilovs.andrejs.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record UserLoginRequest(
  @NotNull(message = "Username is required")
  @NotBlank(message = "Username is required")
  @Schema(description = "Username", defaultValue = "username")
  @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
  String username,

  @NotNull(message = "Password is required")
  @NotBlank(message = "Password is required")
  @Schema(description = "Password", defaultValue = "StrongPass123")
  String password
) {
}
