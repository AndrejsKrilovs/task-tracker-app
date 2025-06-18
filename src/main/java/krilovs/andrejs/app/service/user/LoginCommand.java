package krilovs.andrejs.app.service.user;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import krilovs.andrejs.app.dto.UserLoginRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.mapper.user.UserMapper;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.PasswordService;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequestScoped
public class LoginCommand implements ServiceCommand<UserLoginRequest, UserResponse> {
  @Inject
  UserRepository userRepository;

  @Inject
  PasswordService passwordService;

  @Inject
  UserMapper userMapper;

  @Override
  public UserResponse execute(UserLoginRequest input) {
    Optional<User> userFromDatabase = userRepository.findUserByUsername(input.username());
    String hashedPassword = userFromDatabase.map(User::getPassword).orElse("");

    log.info("Verifying password");
    if (userFromDatabase.isPresent() && passwordService.verifyPassword(input.password(), hashedPassword)) {
      log.info("User logged successfully");
      return userMapper.toDto(userFromDatabase.get());
    }

    log.error("User not exist. Please check credentials");
    throw new UserException("User not exist. Please check credentials");
  }
}
