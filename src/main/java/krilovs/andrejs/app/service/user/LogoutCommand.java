package krilovs.andrejs.app.service.user;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.ServiceCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class LogoutCommand implements ServiceCommand<String, Void> {
  @Inject
  UserRepository userRepository;

  @Override
  @Transactional
  public Void execute(String user) {
    int updatedRows = userRepository.updateUserLastVisitForUser(user);
    if (updatedRows == 1) {
      userRepository.updateUserLastVisitForUser(user);
      return null;
    }

    log.error("Last visit date not updated. User '{}' is not active.", user);
    throw new UserUnauthorizedException("Last visit date not updated. User '%s' is not active.".formatted(user));
  }
}
