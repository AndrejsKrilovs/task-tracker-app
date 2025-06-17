package krilovs.andrejs.app.mapper.user;

import krilovs.andrejs.app.dto.UserPermissions;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "jakarta")
public interface UserMapper {
  @Mapping(target = "role", defaultValue = "UNKNOWN")
  @Mapping(target = "userPermissions", source = "role", qualifiedByName = "mapPermissions")
  UserResponse toDto(User user);

  @Mapping(target = "tasks", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "lastVisitAt", ignore = true)
  @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
  User toEntity(UserRegistrationRequest request);

  @Named("mapRole")
  default UserRole mapRole(String roleStr) {
    if (roleStr.isEmpty()) {
      return UserRole.UNKNOWN;
    }
    try {
      return UserRole.valueOf(roleStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      return UserRole.UNKNOWN;
    }
  }

  @Named("mapPermissions")
  default List<UserPermissions> mapPermissions(String role) {
    return switch (UserRole.valueOf(role)) {
      case BUSINESS_ANALYST, PRODUCT_OWNER, SCRUM_MASTER -> Arrays.asList(UserPermissions.values());
      default -> List.of(UserPermissions.CAN_SEE_TASK_STATUSES);
    };
  }
}
