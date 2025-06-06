package krilovs.andrejs.app.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import krilovs.andrejs.app.dto.CreateTaskRequest;
import krilovs.andrejs.app.dto.ExceptionResponse;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.dto.UserResponse;
import krilovs.andrejs.app.service.ServiceCommandExecutor;
import krilovs.andrejs.app.service.task.CreateCommand;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Slf4j
@Path("/api/v1/task-tracker/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Task API", description = "Task operation endpoint")
public class TaskFacade {

  @Inject
  ServiceCommandExecutor executor;

  @POST
  @Path("/create")
  @Operation(summary = "Create new task", description = "Creates new task with provided credentials")
  @APIResponses(value = {
    @APIResponse(responseCode = "201", description = "Task successfully created",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
    @APIResponse(responseCode = "400", description = "Incorrect task credentials",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    @APIResponse(responseCode = "401", description = "Not authorized user",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    @APIResponse(responseCode = "409", description = "User role not allowed to create tasks",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public Response createTask(@Valid CreateTaskRequest request) {
    log.info("Requested to create new task '{}'", request);
    TaskResponse result = executor.run(CreateCommand.class, request);
    log.info("Successfully created task '{}' with status '{}'", request, Response.Status.CREATED);
    return Response.status(Response.Status.CREATED).entity(result).build();
  }
}
