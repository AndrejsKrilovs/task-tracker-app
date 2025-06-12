package krilovs.andrejs.app.repository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import krilovs.andrejs.app.entity.Task;
import krilovs.andrejs.app.entity.TaskStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequestScoped
public class TaskRepository {
  @Inject
  EntityManager entityManager;

  public void persistTask(Task taskEntity) {
    entityManager.persist(taskEntity);
    log.info("Persisted task '{}' into database", taskEntity.getTitle());
  }

  public void updateTask(Task taskEntity) {
    String taskTitle = Optional.ofNullable(taskEntity.getTitle()).orElse("");
    String taskDescription = Optional.ofNullable(taskEntity.getDescription()).orElse("");
    TaskStatus taskStatus = Optional.ofNullable(taskEntity.getStatus()).orElse(TaskStatus.UNKNOWN);

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Task> update = cb.createCriteriaUpdate(Task.class);
    Root<Task> root = update.from(Task.class);

    if (!taskTitle.isBlank()) {
      update.set("title", taskTitle);
    }
    if (!taskDescription.isBlank()) {
      update.set("description", taskDescription);
    }
    if (taskStatus != TaskStatus.UNKNOWN) {
      update.set("status", taskStatus);
    }

    update.set("user", taskEntity.getUser());
    update.where(cb.equal(root.get("id"), taskEntity.getId()));
    entityManager.createQuery(update).executeUpdate();
    log.info("Updated task to {} into database", taskEntity);
  }

  public List<Task> findTasksByStatus(TaskStatus status, int offset, int limit) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Task> select = cb.createQuery(Task.class);
    Root<Task> root = select.from(Task.class);

    ParameterExpression<TaskStatus> statusParam = cb.parameter(TaskStatus.class, "status");
    select.select(root)
      .where(cb.equal(root.get("status"), statusParam))
      .orderBy(cb.desc(root.get("createdAt")));

    log.info("Finding tasks with status '{}'", status);
    return entityManager.createQuery(select)
      .setParameter("status", status)
      .setFirstResult(offset)
      .setMaxResults(limit)
      .getResultList();
  }
}
