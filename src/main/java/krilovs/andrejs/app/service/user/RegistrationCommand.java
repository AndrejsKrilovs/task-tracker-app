package krilovs.andrejs.app.service.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import krilovs.andrejs.app.mapper.UserMapper;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.PasswordService;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@ApplicationScoped
public class RegistrationCommand implements ServiceCommand<UserRegistrationRequest, UserResponse> {
  @Inject
  UserRepository userRepository;

  @Inject
  UserMapper userMapper;

  @Inject
  PasswordService passwordService;

  @Override
  @Transactional
  public UserResponse execute(UserRegistrationRequest input) {
    if (userRepository.findUserByUsername(input.username()).isEmpty()) {
      return persistAndRegisterUser(input);
    }

    log.error("User not created. User with username '{}' already exists", input.username());
    throw new UserException("User not created. User with username '%s' already exists".formatted(input.username()));
  }

  private UserResponse persistAndRegisterUser(UserRegistrationRequest input) {
    User userEntity = userMapper.toEntity(input);
    userEntity.setPassword(passwordService.hashPassword(input.password()));
    userEntity.setCreatedAt(LocalDateTime.now());
    userRepository.persistUser(userEntity);

    return userMapper.toDto(userEntity);
  }
}
