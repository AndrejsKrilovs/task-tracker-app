package krilovs.andrejs.app.controller;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.entity.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

@QuarkusTest
@TestHTTPEndpoint(UserFacade.class)
class UserRegistrationTest {
  @Inject
  EntityManager entityManager;

  @BeforeEach
  @Transactional
  void setUp() {
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
    String sql = "insert into test.user_table (ut_username, ut_password) values ('username', 'hashedPwd')";
    entityManager.createNativeQuery(sql).executeUpdate();
  }

  @AfterEach
  @Transactional
  void tearDown() {
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
  }

  @Test
  void shouldRegisterNewUserAndReturnCreated() {
    var request = new UserRegistrationRequest(
      "test-user", "TestPass", "test-user@email.com", UserRole.SOFTWARE_DEVELOPER
    );

    RestAssured.given()
      .contentType(ContentType.JSON)
      .body(request)
      .when()
      .post("/register")
      .then()
      .statusCode(201)
      .body("username", Matchers.equalTo("test-user"))
      .body("email", Matchers.equalTo("test-user@email.com"))
      .body("role", Matchers.equalTo("SOFTWARE_DEVELOPER"))
      .body("createdAt", Matchers.notNullValue());
  }

  @Test
  void shouldNotRegisterNewUserAndReturnBadRequest() {
    var request = new UserRegistrationRequest(
      null, null, null, null
    );

    Map<String, String> errorMap = Map.of(
      "registerUser.request.username", "Username is required",
      "registerUser.request.password", "Password is required"
    );

    RestAssured.given()
      .contentType(ContentType.JSON)
      .body(request)
      .when()
      .post("/register")
      .then()
      .statusCode(400)
      .body("status", Matchers.equalTo("BAD_REQUEST"))
      .body("timestamp", Matchers.notNullValue())
      .body("path", Matchers.equalTo("/api/v1/task-tracker/users/register"))
      .body("message", Matchers.is(errorMap));
  }

  @Test
  void shouldNotRegisterDuplicateUserAndReturnConflict() {
    var request = new UserRegistrationRequest(
      "username", "hashedPwd", null, null
    );

    RestAssured.given()
      .contentType(ContentType.JSON)
      .body(request)
      .when()
      .post("/register")
      .then()
      .statusCode(409)
      .body("status", Matchers.equalTo("CONFLICT"))
      .body("timestamp", Matchers.notNullValue())
      .body("path", Matchers.equalTo("/api/v1/task-tracker/users/register"))
      .body("message", Matchers.equalTo("User with username 'username' already exists"));
  }
}