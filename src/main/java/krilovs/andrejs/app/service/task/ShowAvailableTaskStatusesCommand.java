package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import krilovs.andrejs.app.dto.TaskStatusResponse;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestScoped
public class ShowAvailableTaskStatusesCommand implements ServiceCommand<Void, TaskStatusResponse> {

  @Inject
  JsonWebToken jsonWebToken;

  private static final Map<UserRole, List<TaskStatus>> ROLE_STATUSES = Map.of(
    UserRole.PRODUCT_OWNER, Arrays.asList(TaskStatus.values()),
    UserRole.SCRUM_MASTER, Arrays.asList(TaskStatus.values()),
    UserRole.BUSINESS_ANALYST, List.of(
      TaskStatus.READY_FOR_DEVELOPMENT,
      TaskStatus.COMPLETED
    ),
    UserRole.SOFTWARE_DEVELOPER, List.of(
      TaskStatus.READY_FOR_DEVELOPMENT,
      TaskStatus.IN_DEVELOPMENT,
      TaskStatus.CODE_REVIEW,
      TaskStatus.REOPEN
    ),
    UserRole.QA_SPECIALIST, List.of(
      TaskStatus.READY_FOR_TEST,
      TaskStatus.IN_TESTING,
      TaskStatus.COMPLETED
    )
  );

  @Override
  public TaskStatusResponse execute(Void input) {
    UserRole userRole = jsonWebToken.getGroups()
      .stream()
      .map(UserRole::valueOf)
      .findFirst()
      .orElse(UserRole.UNKNOWN);

    List<TaskStatus> taskRoles = ROLE_STATUSES.getOrDefault(userRole, List.of())
      .stream()
      .filter(role -> role != TaskStatus.UNKNOWN)
      .toList();
    log.info("Selected '{}' statuses", taskRoles);
    return new TaskStatusResponse(taskRoles);
  }
}
