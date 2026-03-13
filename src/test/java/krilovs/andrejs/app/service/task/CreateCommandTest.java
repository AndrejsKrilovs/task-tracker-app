package krilovs.andrejs.app.service.task;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCommandTest {

  @InjectMocks
  CreateCommand createCommand;

  @Mock
  TaskRepository taskRepository;

  @Mock
  TaskMapper taskMapper;

  User userEntity;
  Task taskEntity;
  Validator validator;

  @BeforeEach
  void setUp() {
    try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      validator = validatorFactory.getValidator();
    }

    userEntity = new User();
    userEntity.setUsername("username");
    userEntity.setRole(UserRole.PRODUCT_OWNER);

    taskEntity = new Task();
    taskEntity.setId(1L);
    taskEntity.setTitle("Test task");
    taskEntity.setUser(userEntity);
    taskEntity.setCreatedAt(LocalDateTime.now());
    taskEntity.setDescription("Some task description");
  }

  @ParameterizedTest(name = "title={0}, description={1}")
  @CsvSource({
    "Test task,''",
    "Test task,Some task description"
  })
  void shouldRegisterNewTaskSuccessfully(String title, String description) {
    CreateUpdateTaskRequest request = new CreateUpdateTaskRequest(null, title, description, null, "username");
    prepareMocks(request);

    TaskResponse response = createCommand.execute(request);

    assertEquals("username", response.user());
    assertEquals("Test task", response.title());
    assertNotNull(response.createdAt());
    assertEquals(TaskStatus.READY_FOR_DEVELOPMENT, response.status());
    assertNull(response.modifiedAt());

    verify(taskRepository).persistTask(taskEntity);
  }

  @ParameterizedTest
  @MethodSource("validRequests")
  void shouldPassValidationsWithValidValues(CreateUpdateTaskRequest request) {
    Set<ConstraintViolation<CreateUpdateTaskRequest>> violations = validator.validate(request);
    assertTrue(violations.isEmpty());
  }

  @ParameterizedTest
  @MethodSource("invalidRequests")
  void shouldFailValidationsWithIncorrectValues(CreateUpdateTaskRequest request) {
    Set<ConstraintViolation<CreateUpdateTaskRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty());
  }

  static Stream<Arguments> validRequests() {
    return Stream.of(
      Arguments.of(new CreateUpdateTaskRequest(null,"Some task", null, null, null)),
      Arguments.of(new CreateUpdateTaskRequest(null, "Some new task", "Some description", null, null))
    );
  }

  static Stream<Arguments> invalidRequests() {
    return Stream.of(
      Arguments.of(new CreateUpdateTaskRequest(null, null, "Description", null, null)),
      Arguments.of(new CreateUpdateTaskRequest(null, "", "Description", null, null)),
      Arguments.of(new CreateUpdateTaskRequest(null, "T", "Short title", null, null))
    );
  }

  private void prepareMocks(CreateUpdateTaskRequest request) {
    when(taskMapper.toEntity(request)).thenReturn(taskEntity);
    when(taskMapper.toDto(any())).thenReturn(
      new TaskResponse(
        taskEntity.getId(),
        taskEntity.getTitle(),
        taskEntity.getDescription(),
        TaskStatus.READY_FOR_DEVELOPMENT,
        taskEntity.getCreatedAt(),
        taskEntity.getModifiedAt(),
        userEntity.getUsername()
      )
    );
  }
}
