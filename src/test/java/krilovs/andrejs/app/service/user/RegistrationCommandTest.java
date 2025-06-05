package krilovs.andrejs.app.service.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.UserMapper;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.PasswordService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

  Validator validator;

  UserRegistrationRequest validRequest;
  User userEntity;

  @BeforeEach
  void setUp() {
    try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      validator = validatorFactory.getValidator();
    }

    validRequest = new UserRegistrationRequest(
      "username", "password", "some@test.email", UserRole.SOFTWARE_DEVELOPER
    );

    userEntity = new User();
    userEntity.setUsername("username");
    userEntity.setEmail("some@test.email");
    userEntity.setRole(UserRole.SOFTWARE_DEVELOPER);
  }

  @Test
  void shouldRegisterNewUserSuccessfully() {
    Mockito.when(userRepository.findUserByUsername("username")).thenReturn(Optional.empty());
    Mockito.when(userMapper.toEntity(validRequest)).thenReturn(userEntity);
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

    UserResponse response = registrationCommand.execute(validRequest);

    Assertions.assertEquals("username", response.username());
    Assertions.assertEquals("some@test.email", response.email());
    Assertions.assertEquals(UserRole.SOFTWARE_DEVELOPER, response.role());
    Assertions.assertNotNull(response.createdAt());

    Mockito.verify(userRepository).persistUser(userEntity);
    Assertions.assertEquals("hashedPassword", userEntity.getPassword());
  }

  @Test
  void shouldThrowExceptionWhenUserAlreadyExists() {
    Mockito.when(userRepository.findUserByUsername("username")).thenReturn(Optional.of(userEntity));

    UserException ex = Assertions.assertThrows(UserException.class, () -> registrationCommand.execute(validRequest));
    Assertions.assertTrue(ex.getMessage().contains("already exists"));

    Mockito.verify(userRepository, Mockito.never()).persistUser(Mockito.any(User.class));
  }

  @Test
  void shouldPassValidationsWithValidRequest() {
    Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(validRequest);
    Assertions.assertTrue(violations.isEmpty());
  }

  @ParameterizedTest
  @MethodSource("invalidRequests")
  void shouldFailValidationsWithIncorrectRequest(UserRegistrationRequest request) {
    Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
    Assertions.assertFalse(violations.isEmpty());
  }

  static Stream<Arguments> invalidRequests() {
    return Stream.of(
      Arguments.of(new UserRegistrationRequest(null, "pass", "email@test.com", UserRole.SOFTWARE_DEVELOPER)),
      Arguments.of(new UserRegistrationRequest("user", null, "email@test.com", UserRole.SOFTWARE_DEVELOPER)),
      Arguments.of(new UserRegistrationRequest("user", "pass", "bad-email", UserRole.SOFTWARE_DEVELOPER))
    );
  }
}
