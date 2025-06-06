package krilovs.andrejs.app.repository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import krilovs.andrejs.app.entity.Task;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class TaskRepository {
  @Inject
  EntityManager entityManager;

  public void persistTask(Task taskEntity) {
    entityManager.persist(taskEntity);
    log.info("Persisted task '{}' into database", taskEntity.getTitle());
  }
}
