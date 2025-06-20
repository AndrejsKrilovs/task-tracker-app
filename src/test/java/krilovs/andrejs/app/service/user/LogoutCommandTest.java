package krilovs.andrejs.app.service.user;

import krilovs.andrejs.app.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogoutCommandTest {
  @InjectMocks
  LogoutCommand logoutCommand;

  @Mock
  UserRepository userRepository;

  @Test
  void shouldLogoutActiveUser() {
    Mockito.when(userRepository.updateUserLastVisitForUser(Mockito.anyString())).thenReturn(1);
    Assertions.assertNull(logoutCommand.execute("test_user"));
  }

  @Test
  void shouldNotLogoutInactiveUser() {
    Mockito.when(userRepository.updateUserLastVisitForUser(Mockito.anyString())).thenReturn(0);
    UserUnauthorizedException exception = Assertions.assertThrows(
      UserUnauthorizedException.class, () -> logoutCommand.execute("test_user")
    );

    Assertions.assertTrue(exception.getMessage().contains("is not active"));
  }
}