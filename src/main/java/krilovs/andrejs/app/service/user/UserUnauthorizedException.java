package krilovs.andrejs.app.service.user;

public class UserUnauthorizedException extends RuntimeException {
  public UserUnauthorizedException(String message) {
    super(message);
  }
}
