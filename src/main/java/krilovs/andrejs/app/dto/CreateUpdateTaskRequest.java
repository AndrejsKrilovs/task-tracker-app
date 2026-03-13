package krilovs.andrejs.app.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.mapper.task.TaskStatusDeserializer;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to create or update task")
public record CreateUpdateTaskRequest (

  @Schema(
    title = "Identifier",
    defaultValue = "0",
    description = "Task identifier. This parameter is passed from request path"
  )
  Long id,

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
  String title,

  @Schema(
    title = "Description",
    description = "Describe task actions",
    defaultValue = "Some task description"
  )
  String description,

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
  TaskStatus status,

  @Schema(
    title = "User",
    description = "User whom assigned this task",
    defaultValue = "username"
  )
  String user
) {}
