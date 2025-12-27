package krilovs.andrejs.app.service.user;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.UserProfileRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.mapper.user.UserMapper;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.PasswordService;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@RequestScoped
public class UpdateProfileCommand implements ServiceCommand<UserProfileRequest, UserResponse> {
  @Inject
  UserRepository userRepository;

  @Inject
  UserMapper userMapper;

  @Inject
  PasswordService passwordService;

  @Override
  @Transactional
  public UserResponse execute(UserProfileRequest input) {
    User user = userRepository
      .findUserByUsername(input.username())
      .orElseThrow(() -> new UserException("User with username '%s' not found".formatted(input.username())));

    String password = Objects.requireNonNullElse(input.password(), "");
    if (!password.isBlank()) {
      log.info("Hashing password");
      user.setPassword(passwordService.hashPassword(password));
    }

    log.info("Updating profile data");
    userRepository.updateUserProfile(user, input);
    return userMapper.toDto(user);
  }
}