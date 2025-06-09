package krilovs.andrejs.app.service.task;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class CreateCommandTest {
  @InjectMocks
  CreateCommand createCommand;

  @Mock
  TaskRepository taskRepository;

  @Mock
  JsonWebToken jsonWebToken;

  @Mock
  TaskMapper taskMapper;

  Task taskEntity;
  Validator validator;

  @BeforeEach
  void setUp() {
    try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      validator = validatorFactory.getValidator();
    }

    taskEntity = new Task();
    taskEntity.setTitle("Test task");
  }

  @ParameterizedTest(name = "title={0}, description={1}")
  @CsvSource(value = {
    "Test task,''",
    "Test task,Some task description"
  })
  void shouldRegisterNewTaskSuccessfully(String title, String description) {
    CreateUpdateTaskRequest request = new CreateUpdateTaskRequest(title, description);
    Mockito.when(jsonWebToken.getName()).thenReturn("username");
    Mockito.when(jsonWebToken.getGroups()).thenReturn(Set.of("PRODUCT_OWNER"));

    Mockito.when(taskMapper.toEntity(request)).thenReturn(taskEntity);
    Mockito.when(taskMapper.toDto(Mockito.any())).thenAnswer(invocation -> {
      Task task = invocation.getArgument(0);
      return new TaskResponse(
        task.getId(),
        task.getTitle(),
        task.getDescription(),
        task.getStatus(),
        task.getCreatedAt(),
        task.getUser().getUsername()
      );
    });

    TaskResponse response = createCommand.execute(request);

    Assertions.assertEquals("username", response.user());
    Assertions.assertEquals("Test task", response.title());
    Assertions.assertNotNull(response.createdAt());
    Assertions.assertNotNull(response.status());
    Assertions.assertEquals(TaskStatus.READY_FOR_DEVELOPMENT, response.status());

    Mockito.verify(taskRepository).persistTask(taskEntity);
  }

  @ParameterizedTest(name = "title={0}, description={1}")
  @CsvSource(value = {
    "Test task,''",
    "Test task,Some task description"
  })
  void shouldThrowExceptionWhenUserRoleAllowedCreateTask(String title, String description) {
    CreateUpdateTaskRequest request = new CreateUpdateTaskRequest(title, description);
    Mockito.when(jsonWebToken.getName()).thenReturn("");
    Mockito.when(jsonWebToken.getGroups()).thenReturn(Set.of("UNKNOWN"));

    TaskException ex = Assertions.assertThrows(TaskException.class, () -> createCommand.execute(request));
    Assertions.assertTrue(ex.getMessage().contains("Cannot create task with user role UNKNOWN"));
    Mockito.verify(taskRepository, Mockito.never()).persistTask(Mockito.any(Task.class));
  }

  @ParameterizedTest
  @MethodSource("validRequests")
  void shouldPassValidationsWithValidValues(CreateUpdateTaskRequest request) {
    Set<ConstraintViolation<CreateUpdateTaskRequest>> violations = validator.validate(request);
    Assertions.assertTrue(violations.isEmpty());
  }

  @ParameterizedTest
  @MethodSource("invalidRequests")
  void shouldFailValidationsWithIncorrectValues(CreateUpdateTaskRequest request) {
    Set<ConstraintViolation<CreateUpdateTaskRequest>> violations = validator.validate(request);
    Assertions.assertFalse(violations.isEmpty());
  }

  static Stream<Arguments> validRequests() {
    CreateUpdateTaskRequest validRequest1 = new CreateUpdateTaskRequest("Test task", null);
    CreateUpdateTaskRequest validRequest2 = new CreateUpdateTaskRequest("Test task", "Some text");

    return Stream.of(Arguments.of(validRequest1, validRequest2));
  }

  static Stream<Arguments> invalidRequests() {
    return Stream.of(Arguments.of(new CreateUpdateTaskRequest(null, "Task description")));
  }
}