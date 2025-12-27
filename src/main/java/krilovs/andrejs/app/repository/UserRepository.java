package krilovs.andrejs.app.repository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import krilovs.andrejs.app.dto.UserProfileRequest;
import krilovs.andrejs.app.entity.Profile;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;
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

  public Integer updateUserLastVisitForUser(String username) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> update = cb.createCriteriaUpdate(User.class);
    Root<User> root = update.from(User.class);

    update.set(root.get("lastVisitAt"), LocalDateTime.now());
    update.where(cb.equal(root.get("username"), username));

    log.info("Updating last visit date for user '{}'", username);
    return entityManager.createQuery(update).executeUpdate();
  }

  public void updateUserProfile(User userEntity, UserProfileRequest request) {
    String name = Objects.requireNonNullElse(request.name(), "");
    String email = Objects.requireNonNullElse(request.email(), "");
    String surname = Objects.requireNonNullElse(request.surname(), "");
    UserRole role = Objects.requireNonNullElse(request.role(), UserRole.UNKNOWN);

    if (!email.isBlank()) {
      userEntity.setEmail(email);
    }
    if (role != UserRole.UNKNOWN) {
      userEntity.setRole(role);
    }
    if (!name.isBlank() || !surname.isBlank()) {
      Profile profile = userEntity.getProfile();
      if (Objects.isNull(profile)) {
        profile = new Profile();
        profile.setUser(userEntity);
        userEntity.setProfile(profile);
      }

      if (!name.isBlank()) {
        profile.setName(name);
      }
      if (!surname.isBlank()) {
        profile.setSurname(surname);
      }
    }
  }
}
