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
import java.util.Objects;

@Mapper(componentModel = "jakarta")
public interface UserMapper {
  @Mapping(target = "role", defaultValue = "UNKNOWN")
  @Mapping(target = "userPermissions", source = "role", qualifiedByName = "mapUserPermissions")
  UserResponse toDto(User user);

  @Mapping(target = "tasks", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "lastVisitAt", ignore = true)
  @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
  User toEntity(UserRegistrationRequest request);

  @Named("mapRole")
  default UserRole mapRole(String roleStr) {
    if (roleStr == null || roleStr.isBlank()) {
      return UserRole.UNKNOWN;
    }

    return switch (roleStr.toUpperCase()) {
      case "PRODUCT_OWNER" -> UserRole.PRODUCT_OWNER;
      case "BUSINESS_ANALYST" -> UserRole.BUSINESS_ANALYST;
      case "SCRUM_MASTER" -> UserRole.SCRUM_MASTER;
      case "SOFTWARE_DEVELOPER" -> UserRole.SOFTWARE_DEVELOPER;
      case "QA_SPECIALIST" -> UserRole.QA_SPECIALIST;
      default -> UserRole.UNKNOWN;
    };
  }

  @Named("mapUserPermissions")
  default List<UserPermissions> mapRole(UserRole userRole) {
    return switch (Objects.requireNonNullElse(userRole, UserRole.UNKNOWN)) {
      case PRODUCT_OWNER, BUSINESS_ANALYST, SCRUM_MASTER -> Arrays.asList(UserPermissions.values());
      default -> List.of();
    };
  }
}
