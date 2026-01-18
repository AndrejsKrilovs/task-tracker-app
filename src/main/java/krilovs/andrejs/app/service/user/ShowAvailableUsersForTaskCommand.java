package krilovs.andrejs.app.service.user;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import krilovs.andrejs.app.dto.AvailableUserResponse;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.user.UserMapper;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequestScoped
public class ShowAvailableUsersForTaskCommand implements ServiceCommand<TaskStatus, AvailableUserResponse> {
  @Inject
  UserRepository userRepository;

  @Inject
  UserMapper userMapper;

  @Override
  public AvailableUserResponse execute(TaskStatus input) {
    List<UserRole> taskStatusCriteria = switch (input) {
      case TaskStatus.IN_DEVELOPMENT,
           TaskStatus.CODE_REVIEW,
           TaskStatus.REOPEN -> List.of(UserRole.SOFTWARE_DEVELOPER);
      case TaskStatus.READY_FOR_TEST,
           TaskStatus.IN_TESTING -> List.of(UserRole.QA_SPECIALIST);
      case TaskStatus.READY_FOR_DEVELOPMENT -> List.of(UserRole.SOFTWARE_DEVELOPER, UserRole.QA_SPECIALIST);
      default -> List.of();
    };

    List<User> selectedUsers = userRepository.findUsersByRole(taskStatusCriteria);
    return new AvailableUserResponse(
      selectedUsers.stream().map(userEntity -> userMapper.toDto(userEntity)).toList()
    );
  }
}