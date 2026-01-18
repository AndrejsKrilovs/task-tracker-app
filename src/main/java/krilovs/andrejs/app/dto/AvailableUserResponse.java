package krilovs.andrejs.app.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "Represents available users for task with selected status for this task")
public record AvailableUserResponse(List<UserResponse> users) {
}
