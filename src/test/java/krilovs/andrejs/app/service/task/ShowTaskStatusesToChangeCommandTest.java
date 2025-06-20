package krilovs.andrejs.app.service.task;

import krilovs.andrejs.app.dto.TaskStatusResponse;
import krilovs.andrejs.app.entity.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShowTaskStatusesToChangeCommandTest {
  @InjectMocks
  ShowTaskStatusesToChangeCommand taskStatusesToChangeCommand;

  @Test
  void shouldChangeStatusFromExistingOne() {
    TaskStatusResponse response = taskStatusesToChangeCommand.execute(TaskStatus.READY_FOR_DEVELOPMENT);
    Assertions.assertFalse(response.statuses().isEmpty());
    Assertions.assertTrue(response.statuses().contains(TaskStatus.IN_DEVELOPMENT));
  }

  @Test
  void shouldChangeStatusFromNotExistingOne() {
    TaskStatusResponse response = taskStatusesToChangeCommand.execute(TaskStatus.COMPLETED);
    Assertions.assertTrue(response.statuses().isEmpty());
  }
}