package krilovs.andrejs.app.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import krilovs.andrejs.app.config.ConfigConstants;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Set;

@Slf4j
@ApplicationScoped
public class JwtService {
  @Inject
  JsonWebToken jsonWebToken;

  public Set<String> getGroups() {
    return jsonWebToken.getGroups();
  }

  public String generateToken(String username, String role) {
    log.info("Generating jwt token for user '{}'", username);
    return Jwt.claims()
      .issuer(ConfigConstants.TASK_TRACKER_APP)
      .upn(username)
      .groups(role)
      .sign();
  }
}
