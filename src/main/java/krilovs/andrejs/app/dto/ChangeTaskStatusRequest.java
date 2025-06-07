package krilovs.andrejs.app.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.mapper.task.TaskStatusDeserializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(title = "Change task status", description = "Request to change task status")
public class ChangeTaskStatusRequest {
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
    description = "User who change task status"
  )
  String username;

  @NotNull(message = "Task status is required")
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

  public ChangeTaskStatusRequest(@NotNull(message = "Username is required")
                                 @NotBlank(message = "Username is required")
                                 String username,

                                 @NotNull(message = "Task status is required")
                                 @JsonDeserialize(using = TaskStatusDeserializer.class)
                                 TaskStatus status) {
    this.username = username;
    this.status = status;
  }
}
