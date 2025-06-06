package krilovs.andrejs.app.mapper.user;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import krilovs.andrejs.app.dto.ExceptionResponse;
import krilovs.andrejs.app.service.user.UserException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Provider
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserExceptionMapper implements ExceptionMapper<UserException> {
  @Context
  UriInfo uriInfo;

  @Override
  public Response toResponse(UserException exception) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(
      Response.Status.CONFLICT,
      LocalDateTime.now(),
      uriInfo.getAbsolutePath().getPath(),
      exception.getMessage()
    );

    return Response.status(Response.Status.CONFLICT)
      .entity(exceptionResponse)
      .build();
  }
}
