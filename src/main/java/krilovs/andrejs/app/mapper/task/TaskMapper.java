package krilovs.andrejs.app.mapper.task;

import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface TaskMapper {
  @Mapping(target = "user", source = "user.username")
  TaskResponse toDto(Task task);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "user.username", source = "user")
  Task toEntity(CreateUpdateTaskRequest request);
}
