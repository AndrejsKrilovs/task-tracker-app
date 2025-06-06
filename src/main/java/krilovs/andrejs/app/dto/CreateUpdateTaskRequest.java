package krilovs.andrejs.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
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
  @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
  @Schema(
    minLength = 4,
    maxLength = 30,
    required = true,
    title = "Username",
    defaultValue = "username",
    description = "User who creates or updates this task"
  )
  String username;

  @NotNull(message = "Task title is required")
  @NotBlank(message = "Task title is required")
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

  public CreateUpdateTaskRequest(String username, String title, String description) {
    this.username = username;
    this.title = title;
    this.description = description;
  }
}
