package krilovs.andrejs.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString(exclude = "user")
@Table(name = "task_table")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task {
  @Id
  @Column(name = "tt_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "tt_title")
  String title;

  @Column(name = "tt_description")
  String description;

  @Column(name = "tt_status")
  @Enumerated(EnumType.STRING)
  TaskStatus status;

  @Column(name = "tt_created")
  LocalDateTime createdAt;

  @Column(name = "tt_modified")
  LocalDateTime modifiedAt;

  @JoinColumn(name = "tt_user_id")
  @ManyToOne(fetch = FetchType.LAZY)
  User user;
}
