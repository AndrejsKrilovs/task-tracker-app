package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

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
  @Transactional
  public TaskResponse execute(CreateUpdateTaskRequest input) {
//    Optional<User> userFromDatabase = userRepository.findUserByUsername(input.getUsername());
//    if (userFromDatabase.isEmpty()) {
//      log.error("User not authorized. Please check username");
//      throw new UserUnauthorizedException("User not authorized. Please check username");
//    }
//
//    UserRole role = Objects.requireNonNullElse(userFromDatabase.get().getRole(), UserRole.UNKNOWN);
//    if (validateIfUserCanUpdateTask(input.getUsername(), role)) {
//      return updateTask(input);
//    }

    log.error("Task not updated. Cannot update task with user role");
    String errorMessage = """
      Task not updated. Cannot update task with user role
      Only BUSINESS_ANALYST, PRODUCT_OWNER or SCRUM_MASTER can update tasks
      """;
    throw new TaskException(errorMessage);
  }

//  private TaskResponse updateTask(CreateUpdateTaskRequest input) {
//    Task taskEntity = taskMapper.toEntity(input);
//    taskEntity.setId(input.getId());
//    taskRepository.updateTask(taskEntity);
//
//    return taskMapper.toDto(taskEntity);
//  }
//
//  private boolean validateIfUserCanUpdateTask(String username, UserRole userRole) {
//    log.info("Validating, if user '{}' with role '{}' is able to update tasks", username, userRole);
//    return userRole == UserRole.BUSINESS_ANALYST ||
//      userRole == UserRole.PRODUCT_OWNER ||
//      userRole == UserRole.SCRUM_MASTER;
//  }
}
