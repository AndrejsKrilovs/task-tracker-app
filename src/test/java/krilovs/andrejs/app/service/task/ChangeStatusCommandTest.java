package krilovs.andrejs.app.service.task;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import krilovs.andrejs.app.dto.ChangeTaskStatusRequest;
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
class ChangeStatusCommandTest {
//  @InjectMocks
//  ChangeStatusCommand changeStatusCommand;
//
//  @Mock
//  TaskRepository taskRepository;
//
//  @Mock
//  UserRepository userRepository;
//
//  @Mock
//  TaskMapper taskMapper;
//
//  User userEntity;
//  Task taskEntity;
//  Validator validator;
//
//  @BeforeEach
//  void setUp() {
//    try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
//      validator = validatorFactory.getValidator();
//    }
//
//    userEntity = new User();
//    userEntity.setUsername("username");
//    userEntity.setRole(UserRole.PRODUCT_OWNER);
//
//    taskEntity = new Task();
//    taskEntity.setId(1L);
//    taskEntity.setTitle("Update task status");
//    taskEntity.setUser(userEntity);
//    taskEntity.setCreatedAt(LocalDateTime.now());
//    taskEntity.setDescription("It is updated");
//    taskEntity.setStatus(TaskStatus.READY_FOR_DEVELOPMENT);
//  }
//
//  @ParameterizedTest(name = "username={0}, status={1}")
//  @CsvSource(value = {
//    "username,IN_DEVELOPMENT",
//    "username,CODE_REVIEW",
//    "username,READY_FOR_TEST",
//    "username,IN_TESTING",
//    "username,REOPEN",
//    "username,COMPLETED"
//  })
//  void shouldUpdateTaskStatusSuccessfully(String username, TaskStatus taskStatus) {
//    ChangeTaskStatusRequest request = new ChangeTaskStatusRequest(username, taskStatus);
//
//    Mockito.when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(userEntity));
//    Mockito.when(taskMapper.toEntity(request)).thenReturn(taskEntity);
//    Mockito.when(taskMapper.toDto(Mockito.any())).thenAnswer(invocation -> {
//      Task task = invocation.getArgument(0);
//      return new TaskResponse(
//        task.getId(),
//        task.getTitle(),
//        task.getDescription(),
//        task.getStatus(),
//        task.getCreatedAt(),
//        task.getUser().getUsername()
//      );
//    });
//
//    TaskResponse response = changeStatusCommand.execute(request);
//
//    Assertions.assertEquals("username", response.user());
//    Assertions.assertEquals("Update task status", response.title());
//    Assertions.assertNotNull(response.status());
//    Assertions.assertNotNull(response.createdAt());
//
//    Mockito.verify(taskRepository, Mockito.only()).updateTask(taskEntity);
//  }
//
//  @ParameterizedTest(name = "username={0}, status={1}")
//  @CsvSource(value = {
//    "username,IN_DEVELOPMENT",
//    "username,CODE_REVIEW",
//    "username,READY_FOR_TEST",
//    "username,IN_TESTING",
//    "username,REOPEN",
//    "username,COMPLETED"
//  })
//  void shouldThrowExceptionWhenUserNotAuthorized(String username, TaskStatus taskStatus) {
//    ChangeTaskStatusRequest request = new ChangeTaskStatusRequest(username, taskStatus);
//    Mockito.when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());
//
//    UserUnauthorizedException ex = Assertions.assertThrows(
//      UserUnauthorizedException.class, () -> changeStatusCommand.execute(request)
//    );
//
//    Assertions.assertTrue(ex.getMessage().contains("not authorized"));
//    Mockito.verify(taskRepository, Mockito.never()).updateTask(Mockito.any(Task.class));
//  }
//
//  @ParameterizedTest(name = "username={0}, status={1}")
//  @CsvSource(value = {
//    "username,IN_DEVELOPMENT",
//    "username,CODE_REVIEW",
//    "username,READY_FOR_TEST",
//    "username,IN_TESTING",
//    "username,REOPEN",
//    "username,COMPLETED"
//  })
//  void shouldThrowExceptionWhenUserRoleAllowedUpdateTaskStatus(String username, TaskStatus taskStatus) {
//    userEntity.setRole(UserRole.UNKNOWN);
//    ChangeTaskStatusRequest request = new ChangeTaskStatusRequest(username, taskStatus);
//    Mockito.when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(userEntity));
//
//    TaskException ex = Assertions.assertThrows(TaskException.class, () -> changeStatusCommand.execute(request));
//    Assertions.assertTrue(ex.getMessage().contains("User 'username' with role UNKNOWN cannot update task status"));
//    Mockito.verify(taskRepository, Mockito.never()).persistTask(Mockito.any(Task.class));
//  }
//
//  @ParameterizedTest
//  @MethodSource("validRequests")
//  void shouldPassValidationsWithValidValues(ChangeTaskStatusRequest request) {
//    Set<ConstraintViolation<ChangeTaskStatusRequest>> violations = validator.validate(request);
//    Assertions.assertTrue(violations.isEmpty());
//  }
//
//  @ParameterizedTest
//  @MethodSource("invalidRequests")
//  void shouldFailValidationsWithIncorrectValues(ChangeTaskStatusRequest request) {
//    Set<ConstraintViolation<ChangeTaskStatusRequest>> violations = validator.validate(request);
//    Assertions.assertFalse(violations.isEmpty());
//  }
//
//  static Stream<Arguments> validRequests() {
//    ChangeTaskStatusRequest development = new ChangeTaskStatusRequest("username", TaskStatus.IN_DEVELOPMENT);
//    ChangeTaskStatusRequest codeReview = new ChangeTaskStatusRequest("username", TaskStatus.CODE_REVIEW);
//    ChangeTaskStatusRequest readyForTest = new ChangeTaskStatusRequest("username", TaskStatus.READY_FOR_TEST);
//    ChangeTaskStatusRequest testing = new ChangeTaskStatusRequest("username", TaskStatus.IN_TESTING);
//    ChangeTaskStatusRequest completed = new ChangeTaskStatusRequest("username", TaskStatus.COMPLETED);
//
//    return Stream.of(Arguments.of(development, codeReview, readyForTest, testing, completed));
//  }
//
//  static Stream<Arguments> invalidRequests() {
//    return Stream.of(
//      Arguments.of(new ChangeTaskStatusRequest(null, null)),
//      Arguments.of(new ChangeTaskStatusRequest("user", null)),
//      Arguments.of(new ChangeTaskStatusRequest(null, TaskStatus.REOPEN))
//    );
//  }
}