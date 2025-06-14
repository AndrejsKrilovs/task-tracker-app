package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Slf4j
@RequestScoped
public class UpdateCommand implements ServiceCommand<CreateUpdateTaskRequest, TaskResponse> {
  @Inject
  TaskRepository taskRepository;

  @Inject
  TaskMapper taskMapper;

  @Inject
  JsonWebToken jsonWebToken;

  @Override
  @Transactional
  public TaskResponse execute(CreateUpdateTaskRequest input) {
    UserRole userRole = jsonWebToken.getGroups()
      .stream()
      .map(UserRole::valueOf)
      .findFirst()
      .orElse(UserRole.UNKNOWN);

    if (validateIfUserCanUpdateTask(input.getUser(), userRole)) {
      return updateTask(input);
    }

    log.error("Task not updated. Cannot update task with user role '{}'", userRole);
    String errorMessage = """
      Task not updated. Cannot update task with user role %s
      Only BUSINESS_ANALYST, PRODUCT_OWNER or SCRUM_MASTER can update tasks
      """.formatted(userRole);
    throw new TaskException(errorMessage);
  }

  private TaskResponse updateTask(CreateUpdateTaskRequest input) {
    Task taskEntity = taskMapper.toEntity(input);
    taskEntity.setId(input.getId());
    taskRepository.updateTask(taskEntity);

    return taskMapper.toDto(taskEntity);
  }

  private boolean validateIfUserCanUpdateTask(String username, UserRole userRole) {
    log.info("Validating, if user '{}' with role '{}' is able to update tasks", username, userRole);
    return userRole != UserRole.UNKNOWN;
  }
}
