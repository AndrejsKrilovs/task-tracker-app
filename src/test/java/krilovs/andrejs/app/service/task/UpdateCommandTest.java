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
class UpdateCommandTest {
  @InjectMocks
  UpdateCommand updateCommand;

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
    taskEntity.setTitle("Updated task");
    taskEntity.setUser(userEntity);
    taskEntity.setCreatedAt(LocalDateTime.now());
    taskEntity.setDescription("Updated task description");
    taskEntity.setStatus(TaskStatus.READY_FOR_DEVELOPMENT);
  }

  @ParameterizedTest(name = "username={0}, title={1}, description={2}")
  @CsvSource(value = {
    "username,Updated task,''",
    "username,Updated task,Updated task description"
  })
  void shouldUpdateTaskSuccessfully(String username, String title, String description) {
    CreateUpdateTaskRequest request = new CreateUpdateTaskRequest(username, title, description);

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

    TaskResponse response = updateCommand.execute(request);

    Assertions.assertEquals("username", response.user());
    Assertions.assertEquals("Updated task", response.title());
    Assertions.assertNotNull(response.status());
    Assertions.assertNotNull(response.createdAt());

    Mockito.verify(taskRepository, Mockito.only()).updateTask(taskEntity);
  }

  @ParameterizedTest(name = "username={0}, title={1}, description={2}")
  @CsvSource(value = {
    "username,Updated task,''",
    "username,Updated task,Updated task description"
  })
  void shouldThrowExceptionWhenUserNotAuthorized(String username, String title, String description) {
    CreateUpdateTaskRequest request = new CreateUpdateTaskRequest(username, title, description);
    Mockito.when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

    UserUnauthorizedException ex = Assertions.assertThrows(
      UserUnauthorizedException.class, () -> updateCommand.execute(request)
    );

    Assertions.assertTrue(ex.getMessage().contains("not authorized"));
    Mockito.verify(taskRepository, Mockito.never()).updateTask(Mockito.any(Task.class));
  }

  @ParameterizedTest(name = "username={0}, title={1}, description={2}")
  @CsvSource(value = {
    "username,Updated task,''",
    "username,Updated task,Updated task description"
  })
  void shouldThrowExceptionWhenUserRoleAllowedUpdateTask(String username, String title, String description) {
    userEntity.setRole(UserRole.UNKNOWN);
    CreateUpdateTaskRequest request = new CreateUpdateTaskRequest(username, title, description);
    Mockito.when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(userEntity));

    TaskException ex = Assertions.assertThrows(TaskException.class, () -> updateCommand.execute(request));
    Assertions.assertTrue(ex.getMessage().contains("Cannot update task with user role UNKNOWN"));
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
    CreateUpdateTaskRequest validRequest1 = new CreateUpdateTaskRequest("username", "Some task", null);
    CreateUpdateTaskRequest validRequest2 = new CreateUpdateTaskRequest(
      "username",
      "Updated task",
      "Updated description"
    );

    return Stream.of(Arguments.of(validRequest1, validRequest2));
  }

  static Stream<Arguments> invalidRequests() {
    return Stream.of(
      Arguments.of(new CreateUpdateTaskRequest(null, "Updated task", "Updated description")),
      Arguments.of(new CreateUpdateTaskRequest("user", null, "Updated description"))
    );
  }
}