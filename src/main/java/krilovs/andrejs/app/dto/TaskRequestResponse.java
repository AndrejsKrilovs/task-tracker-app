package krilovs.andrejs.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import krilovs.andrejs.app.entity.TaskStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Task request or response. Can be shown to user, also can be requested from same user")
public record TaskRequestResponse(
  @Schema(
    readOnly = true,
    title = "Identifier",
    defaultValue = "0",
    description = "Task identifier"
  )
  Long id,

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
  String title,

  @Schema(
    title = "Description",
    description = "Describe task actions",
    defaultValue = "Some task description"
  )
  String description,

  @NotNull(message = "Task status should be defined")
  @NotBlank(message = "Task status should be defined")
  @Schema(
    required = true,
    title = "Status",
    anyOf = TaskStatus.class,
    description = "Shows status for current task",
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
    readOnly = true,
    title = "Created at",
    description = "Shows task identifier"
  )
  LocalDateTime createdAt
) {
}
