package krilovs.andrejs.app.service.user;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import krilovs.andrejs.app.config.ConfigConstants;
import krilovs.andrejs.app.dto.UserLoginRequest;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.PasswordService;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequestScoped
public class LoginCommand implements ServiceCommand<UserLoginRequest, String> {
  @Inject
  UserRepository userRepository;

  @Inject
  PasswordService passwordService;

  @Override
  public String execute(UserLoginRequest input) {
    Optional<User> userFromDatabase = userRepository.findUserByUsername(input.username());
    String hashedPassword = userFromDatabase.map(User::getPassword).orElse("");

    if (userFromDatabase.isPresent() && passwordService.verifyPassword(input.password(), hashedPassword)) {
      return generateJwtToken(userFromDatabase.get());
    }

    log.error("User not exist. Please check credentials");
    throw new UserUnauthorizedException("User not exist. Please check credentials");
  }

  private String generateJwtToken(User user) {
    log.info("Generating jwt token for user '{}'", user.getUsername());
    return Jwt.claims()
      .issuer(ConfigConstants.TASK_TRACKER_APP)
      .upn(user.getUsername())
      .groups(user.getRole().name())
      .sign();
  }
}
