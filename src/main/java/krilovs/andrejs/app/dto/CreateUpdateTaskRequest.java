package krilovs.andrejs.app.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.mapper.task.TaskStatusDeserializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@NoArgsConstructor
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

  @Setter
  @JsonDeserialize(using = TaskStatusDeserializer.class)
  @Schema(
    title = "Status",
    description = "Shows status to change for current task",
    defaultValue = "READY_FOR_DEVELOPMENT",
    enumeration = {
      "READY_FOR_DEVELOPMENT",
      "IN_DEVELOPMENT",
      "CODE_REVIEW",
      "READY_FOR_TEST",
      "IN_TESTING",
      "COMPLETED"
    }
  )
  TaskStatus status;

  @Setter
  String user;

  public CreateUpdateTaskRequest(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public CreateUpdateTaskRequest(String title, String description, TaskStatus status) {
    this.title = title;
    this.description = description;
    this.status = status;
  }
}
