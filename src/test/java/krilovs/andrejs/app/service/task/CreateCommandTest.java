package krilovs.andrejs.app.service.task;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import krilovs.andrejs.app.dto.CreateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.task.TaskMapper;
import krilovs.andrejs.app.repository.TaskRepository;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.user.UserUnauthorizedException;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class CreateCommandTest {
  @InjectMocks
  CreateCommand createCommand;

  @Mock
  TaskRepository taskRepository;

  @Mock
  UserRepository userRepository;

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

  @ParameterizedTest(name = "username={0}, title={1}, description={2}")
  @CsvSource(value = {
    "username,Test task,''",
    "username,Test task,Some task description"
  })
  void shouldRegisterNewTaskSuccessfully(String username, String title, String description) {
    CreateTaskRequest request = new CreateTaskRequest(username, title, description);

    Mockito.when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(userEntity));
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

  @ParameterizedTest(name = "username={0}, title={1}, description={2}")
  @CsvSource(value = {
    "username,Test task,''",
    "username,Test task,Some task description"
  })
  void shouldThrowExceptionWhenUserNotAuthorized(String username, String title, String description) {
    CreateTaskRequest request = new CreateTaskRequest(username, title, description);
    Mockito.when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

    UserUnauthorizedException ex = Assertions.assertThrows(
      UserUnauthorizedException.class, () -> createCommand.execute(request)
    );

    Assertions.assertTrue(ex.getMessage().contains("not authorized"));
    Mockito.verify(taskRepository, Mockito.never()).persistTask(Mockito.any(Task.class));
  }

  @ParameterizedTest(name = "username={0}, title={1}, description={2}")
  @CsvSource(value = {
    "username,Test task,''",
    "username,Test task,Some task description"
  })
  void shouldThrowExceptionWhenUserRoleAllowedCreateTask(String username, String title, String description) {
    userEntity.setRole(UserRole.UNKNOWN);
    CreateTaskRequest request = new CreateTaskRequest(username, title, description);
    Mockito.when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(userEntity));

    TaskException ex = Assertions.assertThrows(TaskException.class, () -> createCommand.execute(request));
    Assertions.assertTrue(ex.getMessage().contains("Cannot create task with user role UNKNOWN"));
    Mockito.verify(taskRepository, Mockito.never()).persistTask(Mockito.any(Task.class));
  }

  @ParameterizedTest
  @MethodSource("validRequests")
  void shouldPassValidationsWithValidValues(CreateTaskRequest request) {
    Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);
    Assertions.assertTrue(violations.isEmpty());
  }

  @ParameterizedTest
  @MethodSource("invalidRequests")
  void shouldFailValidationsWithIncorrectValues(CreateTaskRequest request) {
    Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);
    Assertions.assertFalse(violations.isEmpty());
  }

  static Stream<Arguments> validRequests() {
    CreateTaskRequest validRequest1 = new CreateTaskRequest("username", "Some task", null);
    CreateTaskRequest validRequest2 = new CreateTaskRequest(
      "username",
      "Some new task",
      "Some text about task description"
    );

    return Stream.of(Arguments.of(validRequest1, validRequest2));
  }

  static Stream<Arguments> invalidRequests() {
    return Stream.of(
      Arguments.of(new CreateTaskRequest(null, "Some new task", "Task description")),
      Arguments.of(new CreateTaskRequest("user", null, "Task description"))
    );
  }
}