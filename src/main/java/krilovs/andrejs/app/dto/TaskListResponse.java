package krilovs.andrejs.app.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "Represents list of tasks that was selected by user")
public record TaskListResponse(
  @Schema(
    title = "List of tasks",
    defaultValue = "[]",
    description = "Task lists whose are selected"
  )
  List<TaskResponse> tasks,

  @Schema(
    title = "Total elements count",
    defaultValue = "0",
    description = "Total elements count for tasks with current status"
  )
  Long tasksCount
) {
}
