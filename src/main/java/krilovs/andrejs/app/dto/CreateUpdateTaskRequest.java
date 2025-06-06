package krilovs.andrejs.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to create or update task")
public class CreateUpdateTaskRequest {
  @Schema(
    title = "Identifier",
    defaultValue = "0",
    description = "Task identifier. This parameter passing from request path"
  )
  Long id;

  @NotNull(message = "Username is required")
  @NotBlank(message = "Username is required")
  @Schema(
    minLength = 4,
    maxLength = 30,
    required = true,
    title = "Username",
    defaultValue = "username",
    description = "User who creates or updates this task. This parameter passing from query path"
  )
  String username;

  @NotNull(message = "Title is required")
  @NotBlank(message = "Title is required")
  @Size(min = 5, max = 50, message = "Task title must be between 5 and 50 characters")
  @Schema(
    minLength = 5,
    maxLength = 50,
    required = true,
    title = "Title",
    defaultValue = "Some new task",
    description = "Represents defined task title"
  )
  String title;

  @Schema(
    title = "Description",
    description = "Describe task actions",
    defaultValue = "Some task description"
  )
  String description;

  public CreateUpdateTaskRequest(@NotNull(message = "Username is required")
                                 @NotBlank(message = "Username is required")
                                 String username,

                                 @NotNull(message = "Task title is required")
                                 @NotBlank(message = "Task title is required")
                                 String title,
                                 String description) {
    this.username = username;
    this.title = title;
    this.description = description;
  }
}
