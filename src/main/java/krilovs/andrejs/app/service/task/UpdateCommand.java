package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.ServiceCommand;
import krilovs.andrejs.app.service.user.UserUnauthorizedException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequestScoped
public class UpdateCommand implements ServiceCommand<CreateUpdateTaskRequest, TaskResponse> {
  @Inject
  TaskRepository taskRepository;

  @Inject
  UserRepository userRepository;

  @Inject
  TaskMapper taskMapper;

  @Override
  public TaskResponse execute(CreateUpdateTaskRequest input) {
    Optional<User> userFromDatabase = userRepository.findUserByUsername(input.getUsername());
    if (userFromDatabase.isEmpty()) {
      log.error("User not authorized. Please check username");
      throw new UserUnauthorizedException("User not authorized. Please check username");
    }

    UserRole role = Objects.requireNonNullElse(userFromDatabase.get().getRole(), UserRole.UNKNOWN);
    if (validateIfUserCanUpdateTask(input.getUsername(), role)) {
      return updateTask(input);
    }

    log.error("Task not updated. Cannot update task with user role '{}'", role);
    String errorMessage = """
      Task not updated. Cannot update task with user role %s
      Only BUSINESS_ANALYST, PRODUCT_OWNER or SCRUM_MASTER can update tasks
      """.formatted(role);
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
    return userRole == UserRole.BUSINESS_ANALYST ||
      userRole == UserRole.PRODUCT_OWNER ||
      userRole == UserRole.SCRUM_MASTER;
  }
}
