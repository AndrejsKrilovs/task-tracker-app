package krilovs.andrejs.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@ToString
@Table(name = "user_profile_table")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Profile {
  @Id
  @Column(name = "up_username")
  String username;

  @Column(name = "up_name")
  String name;

  @Column(name = "up_surname")
  String surname;

  @MapsId
  @ToString.Exclude
  @JoinColumn(name = "up_username")
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  User user;
}
