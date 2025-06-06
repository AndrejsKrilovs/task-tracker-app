package krilovs.andrejs.app.mapper.task;

import krilovs.andrejs.app.dto.CreateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface TaskMapper {
  @Mapping(target = "user", source = "user.username")
  TaskResponse toDto(Task task);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "user.username", source = "username")
  Task toEntity(CreateTaskRequest request);
}
