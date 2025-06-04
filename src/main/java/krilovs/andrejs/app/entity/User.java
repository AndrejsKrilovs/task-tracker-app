package krilovs.andrejs.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_table")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
  @Id
  @Column(name = "ut_username")
  String username;

  @Column(name = "ut_password")
  String password;

  @Column(name = "ut_email")
  String email;

  @Column(name = "ut_role")
  @Enumerated(value = EnumType.STRING)
  UserRole role;

  @Column(name = "ut_registered")
  LocalDateTime createdAt;

  @Column(name = "ut_last_visit")
  LocalDateTime lastVisitAt;
}
