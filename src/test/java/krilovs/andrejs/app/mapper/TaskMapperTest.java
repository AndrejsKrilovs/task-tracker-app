package krilovs.andrejs.app.mapper;

import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.mapper.task.TaskMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskMapperTest {
  @InjectMocks
  TaskMapperImpl taskMapper;

  @Test
  void shouldMapToResponseWithUser() {
    User user = new User();
    Task task = new Task();
    user.setUsername("username");
    task.setUser(user);

    TaskResponse response = taskMapper.toDto(task);
    Assertions.assertNotNull(response);
    Assertions.assertEquals("username", response.user());
  }

  @Test
  void shouldMapToResponseWithNullUser() {
    TaskResponse response = taskMapper.toDto(new Task());
    Assertions.assertNotNull(response);
    Assertions.assertNull(response.user());
  }

  @Test
  void shouldNotMapToTaskResponse() {
    Assertions.assertNull(taskMapper.toDto(null));
  }

  @Test
  void shouldMapToTaskEntityEmptyRequest() {
    Assertions.assertNotNull(taskMapper.toEntity(new CreateUpdateTaskRequest()));
  }

  @Test
  void shouldNotMapToTaskEntity() {
    Assertions.assertNull(taskMapper.toEntity(null));
  }
}