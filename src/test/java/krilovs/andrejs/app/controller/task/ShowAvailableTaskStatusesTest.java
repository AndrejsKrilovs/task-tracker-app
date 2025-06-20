package krilovs.andrejs.app.controller.task;

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

import java.util.List;

@QuarkusTest
class ShowAvailableTaskStatusesTest {
  @Inject
  EntityManager entityManager;
  String jwtCookie;

  @BeforeEach
  @Transactional
  void setUp() {
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
    var user = new UserRegistrationRequest(
      "username", "hashedPwd", null, UserRole.PRODUCT_OWNER
    );

    RestAssured.given()
      .contentType(ContentType.JSON)
      .body(user)
      .post("/api/v1/task-tracker/users/register")
      .then()
      .statusCode(201);


    var loginRequest = new UserLoginRequest("username", "hashedPwd");
    var loginResponse = RestAssured.given()
      .contentType(ContentType.JSON)
      .body(loginRequest)
      .when()
      .post("/api/v1/task-tracker/users/login")
      .then()
      .statusCode(200)
      .extract();

    jwtCookie = loginResponse.cookie("auth_token");
  }

  @AfterEach
  @Transactional
  void tearDown() {
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
  }

  @Test
  void shouldShowAvailableStatusesReturnOk() {
    RestAssured.given()
      .cookie("auth_token", jwtCookie)
      .when()
      .get("/api/v1/task-tracker/tasks/statuses")
      .then()
      .statusCode(200)
      .body("statuses", Matchers.not(Matchers.empty()));
  }

  @Test
  void shouldNotShowAvailableStatusesReturnUnauthorized() {
    RestAssured.given()
      .contentType(ContentType.JSON)
      .when()
      .get("/api/v1/task-tracker/tasks/statuses")
      .then()
      .statusCode(401);
  }
}