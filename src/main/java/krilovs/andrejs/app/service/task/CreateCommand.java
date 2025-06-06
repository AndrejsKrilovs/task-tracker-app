package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.CreateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.ServiceCommand;
import krilovs.andrejs.app.service.user.UserUnauthorizedException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequestScoped
public class CreateCommand implements ServiceCommand<CreateTaskRequest, TaskResponse> {
  @Inject
  TaskRepository taskRepository;

  @Inject
  UserRepository userRepository;

  @Inject
  TaskMapper taskMapper;

  @Override
  @Transactional
  public TaskResponse execute(CreateTaskRequest input) {
    Optional<User> userFromDatabase = userRepository.findUserByUsername(input.username());
    if (userFromDatabase.isEmpty()) {
      log.error("User not authorized. Please check username");
      throw new UserUnauthorizedException("User not authorized. Please check username");
    }

    UserRole role = Objects.requireNonNullElse(userFromDatabase.get().getRole(), UserRole.UNKNOWN);
    if (validateIfUserCanCreateTask(input.username(), role)) {
      return persistAndRegisterTask(input);
    }

    log.error("Task not created. Cannot create task with user role '{}'", role);
    String errorMessage = """
      Task not created. Cannot create task with user role %s
      Only BUSINESS_ANALYST, PRODUCT_OWNER or SCRUM_MASTER can create tasks
      """.formatted(role);
    throw new TaskException(errorMessage);
  }

  private TaskResponse persistAndRegisterTask(CreateTaskRequest input) {
    Task taskEntity = taskMapper.toEntity(input);
    taskEntity.setStatus(TaskStatus.READY_FOR_DEVELOPMENT);
    taskEntity.setCreatedAt(LocalDateTime.now());
    taskRepository.persistTask(taskEntity);

    return taskMapper.toDto(taskEntity);
  }

  private boolean validateIfUserCanCreateTask(String username, UserRole userRole) {
    log.info("Validating, if user '{}' with role '{}' is able to create tasks", username, userRole);
    return userRole == UserRole.BUSINESS_ANALYST ||
      userRole == UserRole.PRODUCT_OWNER ||
      userRole == UserRole.SCRUM_MASTER;
  }
}
