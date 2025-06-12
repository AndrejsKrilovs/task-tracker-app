package krilovs.andrejs.app.service.task;

import krilovs.andrejs.app.dto.FindTaskByStatusRequest;
import krilovs.andrejs.app.dto.TaskListResponse;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindCommandTest {

  @InjectMocks
  FindCommand findCommand;

  @Mock
  TaskRepository taskRepository;

  @Mock
  TaskMapper taskMapper;

  Task taskEntity;
  TaskResponse taskResponse;

  @BeforeEach
  void init() {
    taskEntity = new Task();
    taskEntity.setId(1L);
    taskEntity.setTitle("Test task");
    taskEntity.setDescription("Description");
    taskEntity.setStatus(TaskStatus.IN_DEVELOPMENT);
    taskEntity.setCreatedAt(LocalDateTime.now());

    taskResponse = new TaskResponse(
      taskEntity.getId(),
      taskEntity.getTitle(),
      taskEntity.getDescription(),
      taskEntity.getStatus(),
      taskEntity.getCreatedAt(),
      "test_user"
    );
  }

  @Test
  void shouldReturnListOfTasks() {
    FindTaskByStatusRequest request = new FindTaskByStatusRequest(TaskStatus.IN_DEVELOPMENT, 0, 10);
    when(taskRepository.findTasksByStatus(request.status(), request.offset(), request.limit()))
      .thenReturn(List.of(taskEntity));
    when(taskMapper.toDto(taskEntity)).thenReturn(taskResponse);

    TaskListResponse response = findCommand.execute(request);

    assertFalse(response.tasks().isEmpty());
    assertEquals(1, response.tasks().size());
    assertEquals("Test task", response.tasks().getFirst().title());
    assertEquals("test_user", response.tasks().getFirst().user());

    verify(taskRepository).findTasksByStatus(TaskStatus.IN_DEVELOPMENT, 0, 10);
    verify(taskMapper).toDto(taskEntity);
  }

  @Test
  void shouldReturnEmptyListWhenNoTasksFound() {
    FindTaskByStatusRequest request = new FindTaskByStatusRequest(TaskStatus.COMPLETED, 0, 10);
    when(taskRepository.findTasksByStatus(request.status(), request.offset(), request.limit()))
      .thenReturn(List.of());

    TaskListResponse response = findCommand.execute(request);

    assertNotNull(response.tasks());
    assertTrue(response.tasks().isEmpty());

    verify(taskRepository).findTasksByStatus(TaskStatus.COMPLETED, 0, 10);
    verify(taskMapper, never()).toDto(any());
  }
}
