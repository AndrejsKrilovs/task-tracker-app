package krilovs.andrejs.app.dto;

import krilovs.andrejs.app.entity.TaskStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "Represents task statuses available for user, base of his role")
public record TaskStatusResponse(List<TaskStatus> statuses) {
}
