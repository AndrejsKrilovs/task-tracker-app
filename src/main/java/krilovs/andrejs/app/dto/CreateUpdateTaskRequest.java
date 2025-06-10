package krilovs.andrejs.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to create or update task")
public class CreateUpdateTaskRequest {

  @Setter
  @Schema(
    title = "Identifier",
    defaultValue = "0",
    description = "Task identifier. This parameter is passed from request path"
  )
  Long id;

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

  String user;

  public CreateUpdateTaskRequest(String title, String description) {
    this.title = title;
    this.description = description;
  }
}
