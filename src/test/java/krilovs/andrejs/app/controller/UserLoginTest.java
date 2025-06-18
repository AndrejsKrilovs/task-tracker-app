package krilovs.andrejs.app.controller;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.UserLoginRequest;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.entity.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

@QuarkusTest
@TestHTTPEndpoint(UserFacade.class)
class UserLoginTest {
  @Inject
  EntityManager entityManager;

  @BeforeEach
  @Transactional
  void setUp() {
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
    var user = new UserRegistrationRequest(
      "username", "hashedPwd", null, UserRole.SOFTWARE_DEVELOPER
    );

    RestAssured.given()
      .contentType(ContentType.JSON)
      .body(user)
      .post("/register")
      .then()
      .statusCode(201);
  }

  @AfterEach
  @Transactional
  void tearDown() {
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
  }

  @Test
  void shouldLoginAndReturnOkAndCookie() {
    var loginRequest = new UserLoginRequest("username", "hashedPwd");

    RestAssured.given()
      .contentType(ContentType.JSON)
      .body(loginRequest)
      .when()
      .post("/login")
      .then()
      .statusCode(200)
      .header("Set-Cookie", Matchers.containsString("auth_token"))
      .body("username", Matchers.equalTo("username"))
      .body("role", Matchers.equalTo("SOFTWARE_DEVELOPER"));
  }

  @Test
  void shouldNotLoginAndReturnConflict() {
    var loginRequest = new UserLoginRequest("test-user", "some-pwd");

    RestAssured.given()
      .contentType(ContentType.JSON)
      .body(loginRequest)
      .post("/login")
      .then()
      .statusCode(409)
      .body("status", Matchers.equalTo("CONFLICT"))
      .body("timestamp", Matchers.notNullValue())
      .body("path", Matchers.equalTo("/api/v1/task-tracker/users/login"))
      .body("message", Matchers.equalTo("User not exist. Please check credentials"));
  }

  @Test
  void shouldNotLoginAndReturnBadRequest() {
    var request = new UserLoginRequest(
      null, null
    );

    Map<String, String> errorMap = Map.of(
      "login.request.username", "Username is required",
      "login.request.password", "Password is required"
    );

    RestAssured.given()
      .contentType(ContentType.JSON)
      .body(request)
      .when()
      .post("/login")
      .then()
      .statusCode(400)
      .body("status", Matchers.equalTo("BAD_REQUEST"))
      .body("timestamp", Matchers.notNullValue())
      .body("path", Matchers.equalTo("/api/v1/task-tracker/users/login"))
      .body("message", Matchers.is(errorMap));
  }
}
