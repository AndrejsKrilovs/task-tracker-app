package krilovs.andrejs.app.service.task;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.FindTaskByStatusRequest;
import krilovs.andrejs.app.dto.TaskListResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequestScoped
public class FindCommand implements ServiceCommand<FindTaskByStatusRequest, TaskListResponse> {
  @Inject
  TaskRepository taskRepository;

  @Inject
  TaskMapper taskMapper;

  @Override
  @Transactional
  public TaskListResponse execute(FindTaskByStatusRequest input) {
    List<Task> selectedTasks = taskRepository.findTasksByStatus(input.status(), input.offset(), input.limit());
    return new TaskListResponse(
      selectedTasks.stream().map(taskEntity -> taskMapper.toDto(taskEntity)).toList()
    );
  }
}
