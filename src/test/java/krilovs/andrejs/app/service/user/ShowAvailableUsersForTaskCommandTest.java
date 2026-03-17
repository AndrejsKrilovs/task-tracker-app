package krilovs.andrejs.app.service.user;

import krilovs.andrejs.app.dto.AvailableUserResponse;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.user.UserMapper;
import krilovs.andrejs.app.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ShowAvailableUsersForTaskCommandTest {
  @InjectMocks
  ShowAvailableUsersForTaskCommand availableUsersForTaskCommand;

  @Mock
  UserRepository userRepository;

  @Mock
  UserMapper userMapper;

  @ParameterizedTest
  @MethodSource("taskStatusProvider")
  void shouldReturnAvailableUsersForGivenTaskStatus(TaskStatus inputStatus, List<UserRole> expectedRoles) {
    User user = new User();
    user.setUsername("john");

    Mockito.when(userRepository.findUsersByRole(expectedRoles)).thenReturn(List.of(user));
    AvailableUserResponse response = availableUsersForTaskCommand.execute(inputStatus);
    Mockito.verify(userRepository).findUsersByRole(expectedRoles);
    Assertions.assertFalse(response.users().isEmpty());
  }

  static Stream<Arguments> taskStatusProvider() {
    return Stream.of(
      Arguments.of(TaskStatus.IN_DEVELOPMENT, List.of(UserRole.SOFTWARE_DEVELOPER)),
      Arguments.of(TaskStatus.CODE_REVIEW, List.of(UserRole.SOFTWARE_DEVELOPER)),
      Arguments.of(TaskStatus.REOPEN, List.of(UserRole.SOFTWARE_DEVELOPER)),
      Arguments.of(TaskStatus.READY_FOR_TEST, List.of(UserRole.QA_SPECIALIST)),
      Arguments.of(TaskStatus.IN_TESTING, List.of(UserRole.QA_SPECIALIST)),
      Arguments.of(TaskStatus.READY_FOR_DEVELOPMENT, Arrays.stream(UserRole.values()).toList()),
      Arguments.of(TaskStatus.UNKNOWN, List.of())
    );
  }
}