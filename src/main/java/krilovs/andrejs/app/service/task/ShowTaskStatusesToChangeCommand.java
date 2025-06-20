package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import krilovs.andrejs.app.dto.TaskStatusResponse;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestScoped
public class ShowTaskStatusesToChangeCommand implements ServiceCommand<TaskStatus, TaskStatusResponse> {
  private static final Map<TaskStatus, List<TaskStatus>> AVAILABLE_TASK_STATUSES = Map.of(
    TaskStatus.READY_FOR_DEVELOPMENT, List.of(TaskStatus.IN_DEVELOPMENT),
    TaskStatus.IN_DEVELOPMENT, List.of(TaskStatus.CODE_REVIEW),
    TaskStatus.CODE_REVIEW, List.of(TaskStatus.IN_DEVELOPMENT, TaskStatus.READY_FOR_TEST),
    TaskStatus.READY_FOR_TEST, List.of(TaskStatus.IN_TESTING),
    TaskStatus.IN_TESTING, List.of(TaskStatus.REOPEN, TaskStatus.COMPLETED),
    TaskStatus.REOPEN, List.of(TaskStatus.IN_DEVELOPMENT)
  );

  @Override
  public TaskStatusResponse execute(TaskStatus input) {
    List<TaskStatus> taskRoles = AVAILABLE_TASK_STATUSES.getOrDefault(input, List.of());
    log.info("Selected '{}' statuses", taskRoles);
    return new TaskStatusResponse(taskRoles);
  }
}
