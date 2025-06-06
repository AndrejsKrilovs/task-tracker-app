package krilovs.andrejs.app.service.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class RegistrationCommandTest {
  @InjectMocks
  RegistrationCommand registrationCommand;

  @Mock
  UserRepository userRepository;

  @Mock
  UserMapper userMapper;

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
    userEntity.setEmail("some@test.email");
    userEntity.setRole(UserRole.SOFTWARE_DEVELOPER);
  }

  @ParameterizedTest(name = "username={0}, password={1}, email={2}, role={3}")
  @CsvSource(value = {
    "username,password,some@test.email,''",
    "username,password,some@test.email,SOFTWARE_DEVELOPER"
  })
  void shouldRegisterNewUserSuccessfully(String username, String password, String email, String roleString) {
    UserRole role = (roleString == null || roleString.isBlank()) ? null : UserRole.valueOf(roleString);
    UserRegistrationRequest request = new UserRegistrationRequest(username, password, email, role);

    Mockito.when(userRepository.findUserByUsername("username")).thenReturn(Optional.empty());
    Mockito.when(userMapper.toEntity(request)).thenReturn(userEntity);
    Mockito.when(passwordService.hashPassword("password")).thenReturn("hashedPassword");
    Mockito.when(userMapper.toDto(Mockito.any())).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      return new UserResponse(
        user.getUsername(),
        user.getEmail(),
        user.getRole(),
        user.getCreatedAt(),
        user.getLastVisitAt()
      );
    });

    UserResponse response = registrationCommand.execute(request);

    Assertions.assertEquals("username", response.username());
    Assertions.assertEquals("some@test.email", response.email());
    Assertions.assertNotNull(response.role());
    Assertions.assertNotNull(response.createdAt());

    Mockito.verify(userRepository).persistUser(userEntity);
    Assertions.assertEquals("hashedPassword", userEntity.getPassword());
  }

  @ParameterizedTest(name = "username={0}, password={1}, email={2}, role={3}")
  @CsvSource(value = "username,password,some@test.email,SOFTWARE_DEVELOPER")
  void shouldThrowExceptionWhenUserAlreadyExists(String username, String password, String email, UserRole role) {
    UserRegistrationRequest request = new UserRegistrationRequest(username, password, email, role);
    Mockito.when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(userEntity));

    UserException ex = Assertions.assertThrows(UserException.class, () -> registrationCommand.execute(request));
    Assertions.assertTrue(ex.getMessage().contains("already exists"));

    Mockito.verify(userRepository, Mockito.never()).persistUser(Mockito.any(User.class));
  }

  @ParameterizedTest
  @MethodSource("validRequests")
  void shouldPassValidationsWithValidValues(UserRegistrationRequest request) {
    Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
    Assertions.assertTrue(violations.isEmpty());
  }

  @ParameterizedTest
  @MethodSource("invalidRequests")
  void shouldFailValidationsWithIncorrectValues(UserRegistrationRequest request) {
    Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
    Assertions.assertFalse(violations.isEmpty());
  }

  static Stream<Arguments> validRequests() {
    UserRegistrationRequest validRequest = new UserRegistrationRequest(
      "username",
      "pass",
      "email@test.com",
      UserRole.SOFTWARE_DEVELOPER
    );

    return Stream.of(Arguments.of(validRequest));
  }

  static Stream<Arguments> invalidRequests() {
    return Stream.of(
      Arguments.of(new UserRegistrationRequest(null, "pass", "email@test.com", UserRole.SOFTWARE_DEVELOPER)),
      Arguments.of(new UserRegistrationRequest("user", null, "email@test.com", UserRole.SOFTWARE_DEVELOPER)),
      Arguments.of(new UserRegistrationRequest("user", "pass", "bad-email", UserRole.SOFTWARE_DEVELOPER))
    );
  }
}
