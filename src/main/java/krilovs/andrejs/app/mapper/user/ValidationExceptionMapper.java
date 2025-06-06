package krilovs.andrejs.app.mapper.user;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import krilovs.andrejs.app.dto.ExceptionResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Provider
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
  @Context
  UriInfo uriInfo;

  @Override
  public Response toResponse(ConstraintViolationException exception) {
    Map<String, String> errors = new HashMap<>();

    exception.getConstraintViolations()
      .forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

    ExceptionResponse exceptionResponse = new ExceptionResponse(
      Response.Status.BAD_REQUEST,
      LocalDateTime.now(),
      uriInfo.getAbsolutePath().getPath(),
      errors
    );

    return Response.status(Response.Status.BAD_REQUEST)
      .entity(exceptionResponse)
      .build();
  }
}
