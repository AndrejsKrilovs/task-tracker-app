package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@RequestScoped
public class CreateCommand implements ServiceCommand<CreateUpdateTaskRequest, TaskResponse> {
  @Inject
  TaskRepository taskRepository;

  @Inject
  TaskMapper taskMapper;

  @Override
  @Transactional
  public TaskResponse execute(CreateUpdateTaskRequest input) {
    Task taskEntity = taskMapper.toEntity(input);
    taskEntity.setStatus(TaskStatus.READY_FOR_DEVELOPMENT);
    taskEntity.setCreatedAt(LocalDateTime.now());
    taskRepository.persistTask(taskEntity);

    log.info("Task '{}' created successfully", taskEntity.getTitle());
    return taskMapper.toDto(taskEntity);
  }
}
