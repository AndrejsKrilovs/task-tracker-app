package krilovs.andrejs.app.controller;

import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import krilovs.andrejs.app.dto.ExceptionResponse;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.entity.User;
import krilovs.andrejs.app.repository.UserRepository;
import krilovs.andrejs.app.service.PasswordService;
import krilovs.andrejs.app.service.ServiceCommandExecutor;
import krilovs.andrejs.app.service.user.RegistrationCommand;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Optional;
import java.util.Set;

@Slf4j
@PermitAll
@Path("/api/v1/task-tracker/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User API", description = "User operation endpoint")
public class UserFacade {

  @Inject
  ServiceCommandExecutor executor;

  @POST
  @Path("/register")
  @Operation(summary = "New user registration", description = "Creates new user with provided credentials")
  @APIResponses(value = {
    @APIResponse(responseCode = "201", description = "User successfully registered",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
    @APIResponse(responseCode = "400", description = "Incorrect user credentials",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    @APIResponse(responseCode = "409", description = "User with provided login already exists",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public Response registerUser(@Valid UserRegistrationRequest request) {
    log.info("Requested to create user '{}'", request.username());
    UserResponse result = executor.run(RegistrationCommand.class, request);
    log.info("Successfully registered user '{}' with status '{}'", request.username(), Response.Status.CREATED);
    return Response.status(Response.Status.CREATED).entity(result).build();
  }

  @Inject
  UserRepository userRepository;

  @Inject
  PasswordService passwordService;

  @POST
  @Path("/login")
  public Response login() {
    Optional<User> userOpt = userRepository.findUserByUsername("andrejs_krilovs");
    if (userOpt.isEmpty() || !passwordService.verifyPassword("password", userOpt.get().getPassword())) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    String token = Jwt.claims()
      .issuer("task-tracker-app")
      .upn("andrejs_krilovs")
      .groups(Set.of(userOpt.get().getRole().name()))
      .sign();

    return Response.ok(token).build();
  }
}
