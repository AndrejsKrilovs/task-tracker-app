package krilovs.andrejs.app.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import krilovs.andrejs.app.dto.ExceptionResponse;
import krilovs.andrejs.app.dto.UserLoginRequest;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.service.ServiceCommandExecutor;
import krilovs.andrejs.app.service.user.LoginCommand;
import krilovs.andrejs.app.service.user.RegistrationCommand;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Slf4j
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

  @POST
  @Path("/login")
  @Operation(summary = "User authentication", description = "Authentication user with provided credentials")
  @APIResponses(value = {
    @APIResponse(responseCode = "200", description = "Generated jwt token",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
    @APIResponse(responseCode = "401", description = "Authorization failed, incorrect credentials",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public Response login(@Valid UserLoginRequest request) {
    log.info("Requested for login. User: '{}'", request.username());
    String result = executor.run(LoginCommand.class, request);
    log.info("Successfully logged in '{}' with status '{}'", request.username(), Response.Status.OK);
    return Response.status(Response.Status.OK).entity(Map.of("token", result)).build();
  }
}
