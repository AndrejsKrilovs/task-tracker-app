package krilovs.andrejs.app.service.user;

import krilovs.andrejs.app.dto.UserProfileRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.Profile;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.mapper.user.UserMapper;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.PasswordService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UpdateProfileCommandTest {
  @InjectMocks
  UpdateProfileCommand command;

  @Mock
  UserRepository userRepository;

  @Mock
  UserMapper userMapper;

  @Mock
  PasswordService passwordService;

  @Test
  void shouldUpdateProfileWithoutPasswordHashing() {
    User user = new User();
    user.setUsername("user");

    Profile profile = new Profile();
    profile.setName("Name");
    profile.setSurname("Surname");
    user.setProfile(profile);

    UserProfileRequest request = Mockito.mock(UserProfileRequest.class);
    Mockito.when(request.username()).thenReturn("user");
    Mockito.when(request.password()).thenReturn(null);
    Mockito.when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(user));

    Mockito.when(userMapper.toDto(Mockito.any())).thenAnswer(invocation -> {
      User u = invocation.getArgument(0);
      return new UserResponse(
        u.getUsername(),
        u.getEmail(),
        u.getRole(),
        u.getCreatedAt(),
        u.getLastVisitAt(),
        List.of(),
        u.getProfile() != null ? u.getProfile().getName() : null,
        u.getProfile() != null ? u.getProfile().getSurname() : null
      );
    });

    UserResponse result = command.execute(request);
    Assertions.assertNotNull(result);
    Assertions.assertEquals("Name", result.name());
    Assertions.assertEquals("Surname", result.surname());

    Mockito.verify(passwordService, Mockito.never()).hashPassword(Mockito.any());
    Mockito.verify(userRepository).updateUserProfile(user, request);
  }

  @Test
  void shouldHashPasswordWhenProvided() {
    User user = new User();
    user.setUsername("john");

    UserProfileRequest request = Mockito.mock(UserProfileRequest.class);
    Mockito.when(request.username()).thenReturn("john");
    Mockito.when(request.password()).thenReturn("secret");
    Mockito.when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));
    Mockito.when(passwordService.hashPassword("secret")).thenReturn("hashed");
    Mockito.when(userMapper.toDto(Mockito.any())).thenReturn(Mockito.mock(UserResponse.class));
    command.execute(request);

    Assertions.assertEquals("hashed", user.getPassword());
    Mockito.verify(passwordService).hashPassword("secret");
    Mockito.verify(userRepository).updateUserProfile(user, request);
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    UserProfileRequest request = Mockito.mock(UserProfileRequest.class);
    Mockito.when(request.username()).thenReturn("ghost");
    Mockito.when(userRepository.findUserByUsername("ghost")).thenReturn(Optional.empty());

    UserException ex = Assertions.assertThrows(
      UserException.class,
      () -> command.execute(request)
    );

    Assertions.assertTrue(ex.getMessage().contains("ghost"));
    Mockito.verifyNoInteractions(passwordService);
    Mockito.verify(userMapper, Mockito.never()).toDto(Mockito.any());
  }
}