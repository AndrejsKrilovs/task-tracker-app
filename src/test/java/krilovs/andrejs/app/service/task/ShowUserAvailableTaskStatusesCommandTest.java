package krilovs.andrejs.app.service.task;

import krilovs.andrejs.app.dto.TaskStatusResponse;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.service.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ShowUserAvailableTaskStatusesCommandTest {
  @InjectMocks
  ShowUserAvailableTaskStatusesCommand availableTaskStatusesCommand;

  @Mock
  JwtService jwtService;

  @Test
  void shouldShowAvailableTaskStatusesForDefinedRole() {
    Set<String> userRoles = Stream.of(UserRole.values()).map(Enum::name).collect(Collectors.toSet());
    Mockito.when(jwtService.getGroups()).thenReturn(userRoles);
    TaskStatusResponse response = availableTaskStatusesCommand.execute(null);
    Assertions.assertNotNull(response);
    Assertions.assertFalse(response.statuses().isEmpty());
    Assertions.assertTrue(
      response.statuses().containsAll(
        List.of(TaskStatus.READY_FOR_DEVELOPMENT, TaskStatus.IN_DEVELOPMENT, TaskStatus.CODE_REVIEW, TaskStatus.REOPEN)
      )
    );
  }

  @Test
  void shouldShowEmptyTaskStatusesForUndefinedRole() {
    Mockito.when(jwtService.getGroups()).thenReturn(Set.of());
    TaskStatusResponse response = availableTaskStatusesCommand.execute(null);
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.statuses().isEmpty());
  }
}