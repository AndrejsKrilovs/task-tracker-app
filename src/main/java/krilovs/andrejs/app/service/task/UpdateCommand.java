package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class UpdateCommand implements ServiceCommand<CreateUpdateTaskRequest, TaskResponse> {
  @Inject
  TaskRepository taskRepository;

  @Inject
  TaskMapper taskMapper;

  @Override
  @Transactional
  public TaskResponse execute(CreateUpdateTaskRequest input) {
    Task taskEntity = taskMapper.toEntity(input);
    taskEntity.setId(input.getId());
    taskRepository.updateTask(taskEntity);

    log.info("Task '{}' updated successfully", taskEntity.getTitle());
    return taskMapper.toDto(taskEntity);
  }
}
