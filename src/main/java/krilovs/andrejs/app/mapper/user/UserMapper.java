package krilovs.andrejs.app.mapper.user;

import krilovs.andrejs.app.dto.UserPermissions;
import krilovs.andrejs.app.dto.UserProfileRequest;
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
  @Mapping(target = "name", source = "profile.name")
  @Mapping(target = "role", defaultValue = "UNKNOWN")
  @Mapping(target = "surname", source = "profile.surname")
  @Mapping(target = "userPermissions", source = "role", qualifiedByName = "mapUserPermissions")
  UserResponse toDto(User user);

  @Mapping(target = "tasks", ignore = true)
  @Mapping(target = "profile", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "lastVisitAt", ignore = true)
  @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
  User toEntity(UserRegistrationRequest request);

  @Mapping(target = "tasks", ignore = true)
  @Mapping(target = "profile", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "lastVisitAt", ignore = true)
  @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
  User toEntity(UserProfileRequest request);

  @Named("mapRole")
  default UserRole mapRole(String roleStr) {
    if (Objects.requireNonNullElse(roleStr, "").isBlank()) {
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
      case PRODUCT_OWNER -> Arrays.asList(UserPermissions.values());
      case BUSINESS_ANALYST, SCRUM_MASTER ->
        Arrays.stream(UserPermissions.values())
              .filter(p -> p != UserPermissions.CAN_CHANGE_PROFILE_ROLE)
              .toList();
      default -> List.of();
    };
  }
}
