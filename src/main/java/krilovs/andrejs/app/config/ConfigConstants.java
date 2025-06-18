package krilovs.andrejs.app.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigConstants {
  public final static String TASK_TRACKER_APP = "task-tracker-app";
  public final static String AUTH_TOKEN = "auth_token";
  public final static String COOKIE_STRING_AFTER_LOGIN = "%s=%s; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=3600";
  public final static String COOKIE_STRING_BEFORE_LOGOUT = "%s=; Path=/; HttpOnly; Secure; SameSite=Lex; Max-Age=0";
}
