package krilovs.andrejs.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import krilovs.andrejs.app.entity.TaskStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Task request or response. Can be shown to user, also can be requested from same user")
public record TaskResponse(
  @Schema(
    title = "Identifier",
    defaultValue = "0",
    description = "Task identifier"
  )
  Long id,

  @Schema(
    title = "Title",
    defaultValue = "Some new task",
    description = "Represents defined task title"
  )
  @JsonInclude(JsonInclude.Include.NON_NULL)
  String title,

  @Schema(
    title = "Description",
    description = "Describe task actions",
    defaultValue = "Some task description"
  )
  @JsonInclude(JsonInclude.Include.NON_NULL)
  String description,

  @Schema(
    title = "Status",
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
  @JsonInclude(JsonInclude.Include.NON_NULL)
  TaskStatus status,

  @Schema(
    title = "Creation date",
    description = "Shows task creation date"
  )
  @JsonInclude(JsonInclude.Include.NON_NULL)
  LocalDateTime createdAt,

  @Schema(
    title = "Assigned person",
    description = "User whom assigned task at this moment",
    defaultValue = "Some task description"
  )
  String user
) {
}
