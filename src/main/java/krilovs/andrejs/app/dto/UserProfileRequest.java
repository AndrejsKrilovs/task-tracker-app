package krilovs.andrejs.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import krilovs.andrejs.app.entity.UserRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "User registration request. Request to register in our system")
public record UserProfileRequest(
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

  @NotNull(message = "Name is required")
  @NotBlank(message = "Name is required")
  @Size(min = 4, max = 20, message = "Name must be between 4 and 20 characters")
  @Pattern(regexp = "^[A-Za-z][A-Za-z\\- ]{3,}$", message = "Name should be correct")
  @Schema(
    minLength = 4,
    maxLength = 20,
    required = true,
    title = "Name",
    defaultValue = "Name",
    description = "Represents user name"
  )
  String name,

  @NotNull(message = "Surname is required")
  @NotBlank(message = "Surname is required")
  @Size(min = 4, max = 20, message = "Surname must be between 4 and 20 characters")
  @Pattern(regexp = "^[A-Za-z][A-Za-z\\- ]{3,}$", message = "Surname should be correct")
  @Schema(
    minLength = 4,
    maxLength = 20,
    required = true,
    title = "Surname",
    defaultValue = "Surname",
    description = "Represents user surname"
  )
  String surname,

  @Size(min = 4, max = 10, message = "Password must be between 4 and 10 characters")
  @Schema(
    minLength = 4,
    maxLength = 10,
    writeOnly = true,
    title = "Password",
    defaultValue = "password",
    description = "Represents user password"
  )
  String password,

  @Email(message = "Invalid email format")
  @Schema(
    title = "Email",
    defaultValue = "some@test.email",
    description = "Represents user email"
  )
  String email,

  @NotNull(message = "User role is required")
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
