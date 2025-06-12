package krilovs.andrejs.app.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import krilovs.andrejs.app.dto.ChangeTaskStatusRequest;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.ExceptionResponse;
import krilovs.andrejs.app.dto.FindTaskByStatusRequest;
import krilovs.andrejs.app.dto.TaskListResponse;
import krilovs.andrejs.app.dto.TaskResponse;
import krilovs.andrejs.app.dto.TaskStatusResponse;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.service.ServiceCommandExecutor;
import krilovs.andrejs.app.service.task.ChangeStatusCommand;
import krilovs.andrejs.app.service.task.CreateCommand;
import krilovs.andrejs.app.service.task.FindCommand;
import krilovs.andrejs.app.service.task.ShowAvailableTaskStatusesCommand;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

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
  @RolesAllowed({"BUSINESS_ANALYST", "PRODUCT_OWNER", "SCRUM_MASTER"})
  @Operation(summary = "Create new task", description = "Creates new task with provided credentials")
  @APIResponses(value = {
    @APIResponse(responseCode = "201", description = "Task successfully created",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
    @APIResponse(responseCode = "400", description = "Incorrect task credentials",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    @APIResponse(responseCode = "401", description = "Unauthorized user or user role not allow to create task")
  })
  public Response createTask(@Context SecurityContext securityContext,
                             @Valid CreateUpdateTaskRequest request) {
    request.setUser(securityContext.getUserPrincipal().getName());
    log.info("Requested to create new task '{}'", request);
    TaskResponse result = executor.run(CreateCommand.class, request);
    log.info("Successfully created task '{}' with status '{}'", request, Response.Status.CREATED);
    return Response.status(Response.Status.CREATED).entity(result).build();
  }

  @GET
  @Path("/statuses")
  @RolesAllowed({"BUSINESS_ANALYST", "PRODUCT_OWNER", "SCRUM_MASTER", "SOFTWARE_DEVELOPER", "QA_SPECIALIST"})
  @Operation(summary = "Show available user task statuses", description = "Shows available task statuses, based on user role")
  @APIResponses(value = {
    @APIResponse(responseCode = "200", description = "Task statuses whose are available for current user",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
    @APIResponse(responseCode = "401", description = "Unauthorized user or user role not allow to show task statuses")
  })
  public Response getUserStatuses(@Context SecurityContext securityContext) {
    log.info("Requested to show task statuses based on user '{}' role", securityContext.getUserPrincipal().getName());
    TaskStatusResponse response = executor.run(ShowAvailableTaskStatusesCommand.class, null);
    log.info(
      "Successfully showed available task statuses for user '{}' with response status '{}'",
      securityContext.getUserPrincipal().getName(), Response.Status.OK
    );
    return Response.ok(response).build();
  }

  @GET
  @Path("/{status}")
  @RolesAllowed({"BUSINESS_ANALYST", "PRODUCT_OWNER", "SCRUM_MASTER", "SOFTWARE_DEVELOPER", "QA_SPECIALIST"})
  @Operation(
    summary = "Show available tasks for selected status",
    description = "Show available tasks for selected status"
  )
  @APIResponses(value = {
    @APIResponse(responseCode = "200", description = "Available tasks by selected status",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskListResponse.class))),
    @APIResponse(responseCode = "401", description = "Unauthorized user or user role not allow to show tasks")
  })
  public Response getUserTasksByStatus(@PathParam("status") TaskStatus status,
                                       @QueryParam("offset") @DefaultValue("0") int offset,
                                       @QueryParam("limit") @DefaultValue("6") int limit) {
    FindTaskByStatusRequest request = new FindTaskByStatusRequest(status, offset, limit);
    log.info("Requested to show tasks for status '{}'", status);
    TaskListResponse response = executor.run(FindCommand.class, request);
    log.info(
      "Successfully showed {} tasks from {} with response status '{}'",
      response.tasks().size(), response.tasksCount(), Response.Status.OK
    );
    return Response.ok(response).build();
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
//    request.setId(taskId);
//    request.setUsername(username);
//    log.info("Requested to update task '{}'", request);
//    TaskResponse result = executor.run(UpdateCommand.class, request);
//    log.info("Successfully update task '{}' with status '{}'", request, Response.Status.ACCEPTED);
//    return Response.status(Response.Status.ACCEPTED).entity(result).build();
    return Response.status(Response.Status.ACCEPTED).build();
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
