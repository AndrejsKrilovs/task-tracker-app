package krilovs.andrejs.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import krilovs.andrejs.app.entity.TaskStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Represents task that can be created, updated or fetched by user")
public record TaskResponse(

  @Schema(
    title = "Identifier",
    defaultValue = "0",
    description = "Unique identifier of the task"
  )
  Long id,

  @Schema(
    title = "Title",
    defaultValue = "Some new task",
    description = "Short, clear task name"
  )
  String title,

  @Schema(
    title = "Description",
    description = "Detailed task explanation",
    defaultValue = "Some task description"
  )
  @JsonInclude(JsonInclude.Include.NON_NULL)
  String description,

  @Schema(
    title = "Status",
    description = "Current task status",
    defaultValue = "READY_FOR_DEVELOPMENT",
    enumeration = {
      "READY_FOR_DEVELOPMENT",
      "IN_DEVELOPMENT",
      "CODE_REVIEW",
      "READY_FOR_TEST",
      "IN_TESTING",
      "REOPEN",
      "COMPLETED"
    }
  )
  @JsonInclude(JsonInclude.Include.NON_NULL)
  TaskStatus status,

  @Schema(
    title = "Creation date",
    description = "Timestamp of task creation"
  )
  @JsonInclude(JsonInclude.Include.NON_NULL)
  LocalDateTime createdAt,

  @Schema(
    title = "Last modification date",
    description = "Timestamp of task modification"
  )
  @JsonInclude(JsonInclude.Include.NON_NULL)
  LocalDateTime modifiedAt,

  @Schema(
    title = "Last modified by",
    description = "Username who modified the task",
    defaultValue = "john.doe"
  )
  String user

) {}
