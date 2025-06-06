package krilovs.andrejs.app.mapper.task;

import krilovs.andrejs.app.dto.TaskRequestResponse;
import krilovs.andrejs.app.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface TaskMapper {
  TaskRequestResponse toDto(Task task);

  Task toEntity(TaskRequestResponse request);
}
