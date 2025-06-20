package krilovs.andrejs.app.service.task;

import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCommandTest {
  @InjectMocks
  UpdateCommand updateCommand;

  @Mock
  TaskRepository taskRepository;

  @Mock
  TaskMapper taskMapper;

  User userEntity;
  Task taskEntity;

  @BeforeEach
  void setUp() {
    userEntity = new User();
    userEntity.setUsername("username");
    userEntity.setRole(UserRole.PRODUCT_OWNER);

    taskEntity = new Task();
    taskEntity.setId(1L);
    taskEntity.setTitle("Updated task");
    taskEntity.setUser(userEntity);
    taskEntity.setCreatedAt(LocalDateTime.now());
    taskEntity.setStatus(TaskStatus.IN_DEVELOPMENT);
    taskEntity.setDescription("Some updated description");
  }

  @ParameterizedTest(name = "title={0}, description={1}")
  @CsvSource({
    "Updated task,''",
    "Updated task,Some updated description"
  })
  void shouldUpdateTaskSuccessfully(String title, String description) {
    CreateUpdateTaskRequest request = new CreateUpdateTaskRequest(title, description, TaskStatus.IN_DEVELOPMENT);
    request.setUser("username");

    prepareMocks(request);

    TaskResponse response = updateCommand.execute(request);

    assertEquals("username", response.user());
    assertEquals("Updated task", response.title());
    assertNotNull(response.createdAt());
    assertEquals(TaskStatus.IN_DEVELOPMENT, response.status());
    verify(taskRepository).updateTask(taskEntity);
  }

  private void prepareMocks(CreateUpdateTaskRequest request) {
    when(taskMapper.toEntity(request)).thenReturn(taskEntity);
    when(taskMapper.toDto(any())).thenReturn(
      new TaskResponse(
        taskEntity.getId(),
        taskEntity.getTitle(),
        taskEntity.getDescription(),
        taskEntity.getStatus(),
        taskEntity.getCreatedAt(),
        userEntity.getUsername()
      )
    );
  }
}