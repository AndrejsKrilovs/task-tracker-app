package krilovs.andrejs.app.repository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.entity.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequestScoped
public class TaskRepository {
  @Inject
  EntityManager entityManager;

  @Transactional
  public void persistTask(Task taskEntity) {
    entityManager.persist(taskEntity);
    log.info("Persisted task '{}' into database", taskEntity.getTitle());
  }

  @Transactional
  public void updateTask(Task taskEntity) {
    String taskDescription = Optional.ofNullable(taskEntity.getDescription()).orElse("");
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Task> update = cb.createCriteriaUpdate(Task.class);
    Root<Task> root = update.from(Task.class);

    update.set("title", taskEntity.getTitle());
    if (!taskDescription.isBlank()) {
      update.set("description", taskDescription);
    }

    update.where(cb.equal(root.get("id"), taskEntity.getId()));
    entityManager.createQuery(update).executeUpdate();
    log.info("Updated task to {} into database", taskEntity);
  }
}
