package krilovs.andrejs.app.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import krilovs.andrejs.app.dto.ChangeTaskStatusRequest;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.ExceptionResponse;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.service.ServiceCommandExecutor;
import krilovs.andrejs.app.service.task.ChangeStatusCommand;
import krilovs.andrejs.app.service.task.CreateCommand;
import krilovs.andrejs.app.service.task.UpdateCommand;
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
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
    @APIResponse(responseCode = "400", description = "Incorrect task credentials",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    @APIResponse(responseCode = "401", description = "Not authorized user",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    @APIResponse(responseCode = "409", description = "User role not allowed to create tasks",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public Response createTask(@Valid CreateUpdateTaskRequest request) {
    log.info("Requested to create new task '{}'", request);
    TaskResponse result = executor.run(CreateCommand.class, request);
    log.info("Successfully created task '{}' with status '{}'", request, Response.Status.CREATED);
    return Response.status(Response.Status.CREATED).entity(result).build();
  }

  @PUT
  @Path("/update/{taskId}")
  @Operation(summary = "Update existing task", description = "Updates existing task with provided credentials")
  @APIResponses(value = {
    @APIResponse(responseCode = "202", description = "Task successfully updated",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
    @APIResponse(responseCode = "400", description = "Incorrect task credentials",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    @APIResponse(responseCode = "401", description = "Not authorized user",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    @APIResponse(responseCode = "409", description = "User role not allowed to update tasks",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
  })
  public Response updateTask(@PathParam("taskId") Long taskId,
                             @QueryParam("username") String username,
                             @Valid CreateUpdateTaskRequest request) {
    request.setId(taskId);
    request.setUsername(username);
    log.info("Requested to update task '{}'", request);
    TaskResponse result = executor.run(UpdateCommand.class, request);
    log.info("Successfully update task '{}' with status '{}'", request, Response.Status.ACCEPTED);
    return Response.status(Response.Status.ACCEPTED).entity(result).build();
  }

  @PUT
  @Path("/changeStatus/{taskId}")
  @Operation(
    summary = "Update status for existing task",
    description = "Updates status for existing task with provided credentials"
  )
  @APIResponses(value = {
    @APIResponse(responseCode = "202", description = "Task status successfully updated",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
    @APIResponse(responseCode = "400", description = "Incorrect task credentials",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    @APIResponse(responseCode = "401", description = "Not authorized user",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    @APIResponse(
      responseCode = "409",
      description = "User role not allowed to update tasks statuses for to current task status",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))
    )
  })
  public Response changeTaskStatus(@PathParam("taskId") Long taskId,
                                   @QueryParam("username") String username,
                                   @Valid ChangeTaskStatusRequest request) {
    request.setId(taskId);
    request.setUsername(username);
    log.info("Requested to update task status '{}'", request);
    TaskResponse result = executor.run(ChangeStatusCommand.class, request);
    log.info("Successfully updated task status '{}' with result '{}'", request, Response.Status.ACCEPTED);
    return Response.status(Response.Status.ACCEPTED).entity(result).build();
  }
}
