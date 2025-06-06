package krilovs.andrejs.app.mapper.user;

import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface UserMapper {
  @Mapping(target = "role", defaultValue = "UNKNOWN")
  UserResponse toDto(User user);

  @Mapping(target = "tasks", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "lastVisitAt", ignore = true)
  User toEntity(UserRegistrationRequest request);
}
