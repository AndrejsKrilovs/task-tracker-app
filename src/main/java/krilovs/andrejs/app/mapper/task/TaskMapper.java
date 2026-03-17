package krilovs.andrejs.app.mapper.task;

import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface TaskMapper {
  @Mapping(target = "user", source = "user.username")
  @Mapping(target = "assignTo", source = "assignedTo.username")
  TaskResponse toDto(Task task);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "modifiedAt", ignore = true)
  @Mapping(target = "user.username", source = "user")
  @Mapping(target = "assignedTo.username", source = "assignTo")
  Task toEntity(CreateUpdateTaskRequest request);
}
