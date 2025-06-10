package krilovs.andrejs.app.controller;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.service.ServiceCommandExecutor;
import krilovs.andrejs.app.service.task.CreateCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskFacadeTest {

  @Mock
  ServiceCommandExecutor executor;

  @Mock
  SecurityContext securityContext;

  @InjectMocks
  TaskFacade taskFacade;

  @Test
  @DisplayName("Should return 201 when task is created successfully")
  void shouldCreateTaskAndReturn201() {
    CreateUpdateTaskRequest request = validRequest();
    String username = "test_user";
    when(securityContext.getUserPrincipal()).thenReturn(() -> username);
    when(executor.run(eq(CreateCommand.class), any(CreateUpdateTaskRequest.class)))
      .thenReturn(expectedResponse(username));

    try (Response response = taskFacade.createTask(securityContext, request)) {
      assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
      assertNotNull(response.getEntity());

      TaskResponse result = (TaskResponse) response.getEntity();
      assertEquals("Valid title", result.title());
      assertEquals(username, result.user());
    }

    verify(executor, times(1)).run(eq(CreateCommand.class), any());
  }

  @ParameterizedTest(name = "Should return 401 when principal is {0}")
  @MethodSource("unauthorizedPrincipals")
  void shouldReturn401ForUnauthorizedUsers(Principal principal) {
    CreateUpdateTaskRequest request = validRequest();
    when(securityContext.getUserPrincipal()).thenReturn(principal);

    try (Response response = taskFacade.createTask(securityContext, request)) {
      assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
      assertNull(response.getEntity());
    }

    verify(executor, never()).run(any(), any());
  }

  static Stream<Arguments> unauthorizedPrincipals() {
    return Stream.of(
      Arguments.of((Principal) null),
      Arguments.of((Principal) () -> null),
      Arguments.of((Principal) () -> "")
    );
  }

  private CreateUpdateTaskRequest validRequest() {
    return new CreateUpdateTaskRequest("Valid title", "Valid description");
  }

  private TaskResponse expectedResponse(String username) {
    return new TaskResponse(
      1L,
      "Valid title",
      "Valid description",
      TaskStatus.READY_FOR_DEVELOPMENT,
      LocalDateTime.now(),
      username
    );
  }
}
