package krilovs.andrejs.app.service.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import krilovs.andrejs.app.dto.UserLoginRequest;
import krilovs.andrejs.app.dto.UserPermissions;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.user.UserMapper;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.PasswordService;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class LoginCommandTest {
  @InjectMocks
  LoginCommand loginCommand;

  @Mock
  UserRepository userRepository;

  @Mock
  PasswordService passwordService;

  @Mock
  UserMapper userMapper;

  User userEntity;
  Validator validator;

  @BeforeEach
  void setUp() {
    try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      validator = validatorFactory.getValidator();
    }

    userEntity = new User();
    userEntity.setUsername("username");
    userEntity.setRole(UserRole.SOFTWARE_DEVELOPER);
  }

  @ParameterizedTest(name = "username={0}, password={1}")
  @CsvSource(value = "username,password")
  void shouldLoginSuccessfully(String username, String password) {
    UserLoginRequest loginRequest = new UserLoginRequest(username, password);

    Mockito.when(userRepository.findUserByUsername(Mockito.any())).thenReturn(Optional.of(userEntity));
    Mockito.when(passwordService.verifyPassword(Mockito.any(), Mockito.any())).thenReturn(Boolean.TRUE);
    Mockito.when(userMapper.toDto(Mockito.any())).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      return new UserResponse(
        user.getUsername(),
        user.getEmail(),
        user.getRole(),
        user.getCreatedAt(),
        user.getLastVisitAt(),
        List.of(UserPermissions.CAN_SEE_TASK_STATUSES),
        UUID.randomUUID().toString()
      );
    });

    UserResponse result = loginCommand.execute(loginRequest);
    Assertions.assertNotNull(result);
  }

  @ParameterizedTest(name = "username={0}, password={1}")
  @CsvSource(value = "username,password")
  void shouldNotLogin(String username, String password) {
    UserLoginRequest loginRequest = new UserLoginRequest(username, password);
    Mockito.when(userRepository.findUserByUsername(Mockito.any())).thenReturn(Optional.empty());

    UserException exception = Assertions.assertThrows(
      UserException.class, () -> loginCommand.execute(loginRequest)
    );
    Assertions.assertTrue(exception.getMessage().contains("not exist"));
  }

  @ParameterizedTest
  @MethodSource("validRequests")
  void shouldPassValidationsWithValidValues(UserLoginRequest request) {
    Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
    Assertions.assertTrue(violations.isEmpty());
  }

  @ParameterizedTest
  @MethodSource("invalidRequests")
  void shouldFailValidationsWithIncorrectValues(UserLoginRequest request) {
    Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
    Assertions.assertFalse(violations.isEmpty());
  }

  static Stream<Arguments> validRequests() {
    UserLoginRequest validRequest = new UserLoginRequest("username", "pass");
    return Stream.of(Arguments.of(validRequest));
  }

  static Stream<Arguments> invalidRequests() {
    return Stream.of(
      Arguments.of(new UserLoginRequest(null, "pass")),
      Arguments.of(new UserLoginRequest("user", null))
    );
  }
}