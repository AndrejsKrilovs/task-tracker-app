package krilovs.andrejs.app.controller;

import io.quarkus.security.Authenticated;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import krilovs.andrejs.app.config.ConfigConstants;
import krilovs.andrejs.app.dto.ExceptionResponse;
import krilovs.andrejs.app.dto.UserLoginRequest;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.service.ServiceCommandExecutor;
import krilovs.andrejs.app.service.user.LoginCommand;
import krilovs.andrejs.app.service.user.LogoutCommand;
import krilovs.andrejs.app.service.user.RegistrationCommand;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Slf4j
@ApplicationScoped
@Path("/api/v1/task-tracker/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User API", description = "User operation endpoint")
public class UserFacade {

  @Inject
  ServiceCommandExecutor executor;

  @POST
  @PermitAll
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
  @PermitAll
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
    UserResponse result = executor.run(LoginCommand.class, request);

    log.info("Generating jwt token for user '{}'", result.username());
    String generatedToken = Jwt.claims()
      .issuer(ConfigConstants.TASK_TRACKER_APP)
      .upn(result.username())
      .groups(result.role().name())
      .sign();

    log.info("Successfully logged in with status '{}'", Response.Status.OK);
    return Response.status(Response.Status.OK).entity(result)
      .header(
        HttpHeaders.SET_COOKIE,
        ConfigConstants.COOKIE_STRING_AFTER_LOGIN.formatted(ConfigConstants.AUTH_TOKEN, generatedToken)
      )
      .build();
  }

  @GET
  @Authenticated
  @Path("/logout")
  @Operation(summary = "User logout", description = "Logging out from system")
  @APIResponses(value = {
    @APIResponse(responseCode = "200", description = "User logged out from system"),
    @APIResponse(responseCode = "409", description = "User is not active",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public Response logout(@Context SecurityContext securityContext) {
    String username = securityContext.getUserPrincipal().getName();
    log.info("Requested for logout. User: '{}'", username);

    executor.run(LogoutCommand.class, username);
    log.info("Successfully logged out user '{}' with status '{}'", username, Response.Status.OK);
    return Response.status(Response.Status.OK)
      .header(
        HttpHeaders.SET_COOKIE,
        ConfigConstants.COOKIE_STRING_BEFORE_LOGOUT.formatted(ConfigConstants.AUTH_TOKEN)
      )
      .build();
  }
}
