package krilovs.andrejs.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "User login request. Request to login in our system")
public record UserLoginRequest(
  @NotNull(message = "Username is required")
  @NotBlank(message = "Username is required")
  @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
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
  @Size(min = 4, max = 10, message = "Password must be between 4 and 10 characters")
  @Schema(
    minLength = 4,
    maxLength = 10,
    required = true,
    writeOnly = true,
    title = "Password",
    defaultValue = "password",
    description = "Represents user password"
  )
  String password
) {
}
