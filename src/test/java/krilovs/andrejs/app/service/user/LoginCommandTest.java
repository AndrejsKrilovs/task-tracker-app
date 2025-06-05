package krilovs.andrejs.app.service.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import krilovs.andrejs.app.dto.UserLoginRequest;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class LoginCommandTest {
  @InjectMocks
  LoginCommand loginCommand;

  @Mock
  UserRepository userRepository;

  @Mock
  PasswordService passwordService;

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

    String result = loginCommand.execute(loginRequest);
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isBlank());
  }

  @ParameterizedTest(name = "username={0}, password={1}")
  @CsvSource(value = "username,password")
  void shouldNotLogin(String username, String password) {
    UserLoginRequest loginRequest = new UserLoginRequest(username, password);
    Mockito.when(userRepository.findUserByUsername(Mockito.any())).thenReturn(Optional.empty());

    UserUnauthorizedException exception = Assertions.assertThrows(UserUnauthorizedException.class, () -> loginCommand.execute(loginRequest));
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