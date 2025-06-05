package krilovs.andrejs.app.repository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import krilovs.andrejs.app.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequestScoped
public class UserRepository {
  @Inject
  EntityManager entityManager;

  public Optional<User> findUserByUsername(String username) {
    log.info("Finding if user '{}' exists", username);
    return Optional.ofNullable(entityManager.find(User.class, username));
  }

  public void persistUser(User userEntity) {
    entityManager.persist(userEntity);
    log.info("Persisted user '{}' into database", userEntity.getUsername());
  }
}
