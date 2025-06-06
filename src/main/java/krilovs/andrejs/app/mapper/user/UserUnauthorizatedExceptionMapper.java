package krilovs.andrejs.app.mapper.user;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import krilovs.andrejs.app.dto.ExceptionResponse;
import krilovs.andrejs.app.service.user.UserUnauthorizedException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Provider
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUnauthorizatedExceptionMapper implements ExceptionMapper<UserUnauthorizedException> {
  @Context
  UriInfo uriInfo;

  @Override
  public Response toResponse(UserUnauthorizedException exception) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(
      Response.Status.UNAUTHORIZED,
      LocalDateTime.now(),
      uriInfo.getAbsolutePath().getPath(),
      exception.getMessage()
    );

    return Response.status(Response.Status.UNAUTHORIZED)
      .entity(exceptionResponse)
      .build();
  }
}
