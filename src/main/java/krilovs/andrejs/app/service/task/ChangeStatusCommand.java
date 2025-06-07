package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.ChangeTaskStatusRequest;
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

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequestScoped
public class ChangeStatusCommand implements ServiceCommand<ChangeTaskStatusRequest, TaskResponse> {
  @Inject
  TaskRepository taskRepository;

  @Inject
  UserRepository userRepository;

  @Inject
  TaskMapper taskMapper;

  @Override
  @Transactional
  public TaskResponse execute(ChangeTaskStatusRequest input) {
    Optional<User> userFromDatabase = userRepository.findUserByUsername(input.getUsername());
    if (userFromDatabase.isEmpty()) {
      log.error("User not authorized. Please check username");
      throw new UserUnauthorizedException("User not authorized. Please check username");
    }

    UserRole role = Objects.requireNonNullElse(userFromDatabase.get().getRole(), UserRole.UNKNOWN);
    if (validateIfUserCanUpdateTaskStatus(input.getUsername(), role, input.getStatus())) {
      return updateTaskStatus(userFromDatabase.get(), input);
    }

    log.error(
      "Task status not updated. User '{}' with role {} cannot update task status to {}",
      input.getUsername(), role, input.getStatus()
    );

    String errorMessage = """
      Task status not updated. User '%s' with role %s cannot update task status to %s
      """.formatted(input.getUsername(), role, input.getStatus());
    throw new TaskException(errorMessage);
  }

  private TaskResponse updateTaskStatus(User databaseUser, ChangeTaskStatusRequest input) {
    Task taskEntity = taskMapper.toEntity(input);
    taskEntity.setId(input.getId());
    taskEntity.setUser(databaseUser);
    taskRepository.updateTask(taskEntity);

    return taskMapper.toDto(taskEntity);
  }

  private boolean validateIfUserCanUpdateTaskStatus(String username, UserRole userRole, TaskStatus taskStatus) {
    log.info("Validating, if user '{}' with role '{}' is able to update task status to {}", username, userRole, taskStatus);
    boolean validateAdminRoles =
      userRole == UserRole.BUSINESS_ANALYST ||
        userRole == UserRole.PRODUCT_OWNER ||
        userRole == UserRole.SCRUM_MASTER;

    boolean validateSoftwareDeveloperRoles =
      userRole == UserRole.SOFTWARE_DEVELOPER &&
        (
          taskStatus == TaskStatus.IN_DEVELOPMENT ||
            taskStatus == TaskStatus.CODE_REVIEW ||
            taskStatus == TaskStatus.READY_FOR_TEST
        );

    boolean validateQaSpecialistRoles =
      userRole == UserRole.QA_SPECIALIST &&
        (
          taskStatus == TaskStatus.IN_TESTING ||
            taskStatus == TaskStatus.REOPEN ||
            taskStatus == TaskStatus.COMPLETED
        );

    return validateAdminRoles || validateSoftwareDeveloperRoles || validateQaSpecialistRoles;
  }
}
