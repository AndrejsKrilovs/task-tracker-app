package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;

@Slf4j
@RequestScoped
public class CreateCommand implements ServiceCommand<CreateUpdateTaskRequest, TaskResponse> {
  @Inject
  TaskRepository taskRepository;

  @Inject
  TaskMapper taskMapper;

  @Inject
  JsonWebToken jwt;

  private String username;
  private UserRole userRole;

  @Override
  @Transactional
  public TaskResponse execute(CreateUpdateTaskRequest input) {
    username = jwt.getName();
    userRole = UserRole.valueOf(String.join("", jwt.getGroups()));

    if (validateIfUserCanCreateTask()) {
      return persistAndRegisterTask(input);
    }

    log.error("Task not created. Cannot create task with user role '{}'", userRole);
    String errorMessage = """
      Cannot create task with user role %s
      Only BUSINESS_ANALYST, PRODUCT_OWNER or SCRUM_MASTER can create tasks
      """.formatted(userRole);
    throw new TaskException(errorMessage);
  }

  private TaskResponse persistAndRegisterTask(CreateUpdateTaskRequest input) {
    Task taskEntity = taskMapper.toEntity(input);
    taskEntity.setStatus(TaskStatus.READY_FOR_DEVELOPMENT);
    taskEntity.setCreatedAt(LocalDateTime.now());

    User user = new User();
    user.setUsername(username);
    taskEntity.setUser(user);

    taskRepository.persistTask(taskEntity);
    return taskMapper.toDto(taskEntity);
  }

  private boolean validateIfUserCanCreateTask() {
    log.info("Validating, if user '{}' with role '{}' is able to create tasks", username, userRole);
    return userRole == UserRole.BUSINESS_ANALYST ||
      userRole == UserRole.PRODUCT_OWNER ||
      userRole == UserRole.SCRUM_MASTER;
  }
}
