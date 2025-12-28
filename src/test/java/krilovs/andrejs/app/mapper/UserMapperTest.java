package krilovs.andrejs.app.mapper;

import krilovs.andrejs.app.dto.UserPermissions;
import krilovs.andrejs.app.dto.UserProfileRequest;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.Profile;
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
    Assertions.assertNull(result.name());
    Assertions.assertNull(result.surname());
  }

  @ParameterizedTest(name = "username = {0}, email = {1}, roleStr = {2}, name = {3}, surname = {4}")
  @CsvSource(value = {
    "username,test@some.email,PRODUCT_OWNER,Name,Surname"
  })
  void shouldMapToUserResponseWithAllPermissions(String username, String email, String roleStr, String name, String surname) {
    User user = new User();
    user.setEmail(email);
    user.setUsername(username);
    user.setRole(UserRole.valueOf(roleStr));

    Profile profile = new Profile();
    profile.setName(name);
    profile.setSurname(surname);
    user.setProfile(profile);

    UserResponse result = userMapper.toDto(user);
    Assertions.assertEquals("username", result.username());
    Assertions.assertEquals("test@some.email", result.email());
    Assertions.assertEquals(UserRole.valueOf(roleStr), result.role());
    Assertions.assertTrue(result.userPermissions().containsAll(Arrays.asList(UserPermissions.values())));
    Assertions.assertEquals("Name", result.name());
    Assertions.assertEquals("Surname", result.surname());
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
    Assertions.assertNull(result.name());
    Assertions.assertNull(result.surname());
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
  void shouldNotMapToUserEntityForUserRegistration() {
    UserRegistrationRequest request = null;
    Assertions.assertNull(userMapper.toEntity(request));
  }

  @Test
  void shouldNotMapToUserEntityForUserProfile() {
    UserProfileRequest request = null;
    Assertions.assertNull(userMapper.toEntity(request));
  }

  @Test
  void shouldMapWithNullRole() {
    UserProfileRequest request = new UserProfileRequest("", "", "", "", "", null);
    Assertions.assertNotNull(userMapper.toEntity(request));
  }

  @ParameterizedTest(name = "username = {0}, email = {1}, roleStr = {2}, name = {3}, surname = {4}")
  @CsvSource(value = {
    "username,'',SOFTWARE_DEVELOPER,Name,Surname",
    "username,test@some.email,BUSINESS_ANALYST,Name,Surname"
  })
  void shouldNotMapToUserEntityForUserProfileChange(String username, String email, String roleStr, String name, String surname) {
    UserProfileRequest request = new UserProfileRequest(
      username, name, surname, null, email, UserRole.valueOf(roleStr)
    );

    User result = userMapper.toEntity(request);
    Assertions.assertNotNull(result);
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