package krilovs.andrejs.app.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.mapper.task.TaskStatusDeserializer;

public record FindTaskByStatusRequest(@JsonDeserialize(using = TaskStatusDeserializer.class)
                                      TaskStatus status,
                                      Integer offset,
                                      Integer limit) {
}
