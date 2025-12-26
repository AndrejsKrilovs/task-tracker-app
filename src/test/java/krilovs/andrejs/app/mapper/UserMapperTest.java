package krilovs.andrejs.app.mapper;

import krilovs.andrejs.app.dto.UserPermissions;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.user.UserMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
  @InjectMocks
  UserMapperImpl userMapper;

  @ParameterizedTest(name = "username = {0}, email = {1}, roleStr = {2}")
  @CsvSource(value = {
    "username,test@some.email,''",
    "username,test@some.email,SOFTWARE_DEVELOPER",
    "username,test@some.email,QA_SPECIALIST"
  })
  void shouldMapToUserResponseWithNoPermissions(String username, String email, String roleStr) {
    User user = new User();
    user.setEmail(email);
    user.setUsername(username);
    user.setRole((roleStr == null || roleStr.isBlank()) ? null : UserRole.valueOf(roleStr));

    UserResponse result = userMapper.toDto(user);
    Assertions.assertEquals("username", result.username());
    Assertions.assertEquals("test@some.email", result.email());
    Assertions.assertNotNull(result.role());
    Assertions.assertTrue(result.userPermissions().isEmpty());
  }

  @ParameterizedTest(name = "username = {0}, email = {1}, roleStr = {2}")
  @CsvSource(value = {
    "username,test@some.email,PRODUCT_OWNER"
  })
  void shouldMapToUserResponseWithAllPermissions(String username, String email, String roleStr) {
    User user = new User();
    user.setEmail(email);
    user.setUsername(username);
    user.setRole(UserRole.valueOf(roleStr));

    UserResponse result = userMapper.toDto(user);
    Assertions.assertEquals("username", result.username());
    Assertions.assertEquals("test@some.email", result.email());
    Assertions.assertEquals(UserRole.valueOf(roleStr), result.role());
    Assertions.assertTrue(result.userPermissions().containsAll(Arrays.asList(UserPermissions.values())));
  }

  @ParameterizedTest(name = "username = {0}, email = {1}, roleStr = {2}")
  @CsvSource(value = {
    "username,test@some.email,SCRUM_MASTER",
    "username,test@some.email,BUSINESS_ANALYST"
  })
  void shouldMapToUserResponseExceptChangeProfileStatus(String username, String email, String roleStr) {
    User user = new User();
    user.setEmail(email);
    user.setUsername(username);
    user.setRole(UserRole.valueOf(roleStr));

    UserResponse result = userMapper.toDto(user);
    Assertions.assertEquals("username", result.username());
    Assertions.assertEquals("test@some.email", result.email());
    Assertions.assertEquals(UserRole.valueOf(roleStr), result.role());
    Assertions.assertFalse(result.userPermissions().isEmpty());
    Assertions.assertFalse(result.userPermissions().contains(UserPermissions.CAN_CHANGE_PROFILE_ROLE));
  }

  @Test
  void shouldNotMapToUserResponse() {
    Assertions.assertNull(userMapper.toDto(null));
  }

  @ParameterizedTest(name = "username = {0}, email = {1}, roleStr = {2}")
  @CsvSource(value = {
    "username,test@some.email,''",
    "username,test@some.email,BUSINESS_ANALYST"
  })
  void shouldMapToUserEntity(String username, String email, String roleStr) {
    UserRole role = (roleStr == null || roleStr.isBlank()) ? null : UserRole.valueOf(roleStr);
    UserRegistrationRequest request = new UserRegistrationRequest(username, "Password", email, role);
    User entity = userMapper.toEntity(request);

    Assertions.assertEquals("username", entity.getUsername());
    Assertions.assertEquals("test@some.email", entity.getEmail());
    Assertions.assertEquals(role, entity.getRole());
  }

  @Test
  void shouldNotMapToUserEntity() {
    Assertions.assertNull(userMapper.toEntity(null));
  }

  @ParameterizedTest
  @MethodSource("validRoles")
  void shouldMapValidRoleStringToUserRole(String roleFromString) {
    Assertions.assertEquals(UserRole.valueOf(roleFromString), userMapper.mapRole(roleFromString));
  }

  @ParameterizedTest
  @MethodSource("incorrectRoles")
  void shouldMapIncorrectRoleStringToUnknownRole(String roleFromString) {
    Assertions.assertEquals(UserRole.UNKNOWN, userMapper.mapRole(roleFromString));
  }

  @ParameterizedTest(name = "roleStr = {0}")
  @CsvSource(value = {"PRODUCT_OWNER", "BUSINESS_ANALYST", "SCRUM_MASTER"})
  void shouldMapValidRoleToUserPermissions(String roleFromString) {
    UserRole role = UserRole.valueOf(roleFromString);
    Assertions.assertFalse(userMapper.mapRole(role).isEmpty());
  }

  @ParameterizedTest(name = "roleStr = {0}")
  @CsvSource(value = {"''", "SOFTWARE_DEVELOPER", "QA_SPECIALIST"})
  void shouldMapAnyRoleToEmptyList(String roleFromString) {
    UserRole role = (roleFromString == null || roleFromString.isBlank()) ? null : UserRole.valueOf(roleFromString);
    Assertions.assertTrue(userMapper.mapRole(role).isEmpty());
  }

  static Stream<Arguments> validRoles() {
    return Stream.of(
      Arguments.of("PRODUCT_OWNER"),
      Arguments.of("BUSINESS_ANALYST"),
      Arguments.of("SCRUM_MASTER"),
      Arguments.of("SOFTWARE_DEVELOPER"),
      Arguments.of("QA_SPECIALIST")
    );
  }

  static Stream<Arguments> incorrectRoles() {
    return Stream.of(Arguments.of(""), Arguments.of("incorrect_role"));
  }
}