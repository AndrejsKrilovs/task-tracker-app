package krilovs.andrejs.app.controller.task;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.dto.CreateUpdateTaskRequest;
import krilovs.andrejs.app.dto.UserLoginRequest;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.entity.TaskStatus;
import krilovs.andrejs.app.entity.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

@QuarkusTest
class UpdateTaskTest {
  @Inject
  EntityManager entityManager;
  String jwtCookie;
  Long createdTaskId;

  @BeforeEach
  @Transactional
  void setUp() {
    entityManager.createNativeQuery("delete from test.task_table").executeUpdate();
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
    createdTaskId = RestAssured.given()
      .contentType(ContentType.JSON)
      .cookie("auth_token", jwtCookie)
      .body(new CreateUpdateTaskRequest("title", "description"))
      .when()
      .post("/api/v1/task-tracker/tasks/create")
      .then()
      .statusCode(201)
      .extract()
      .body().jsonPath().getLong("id");
  }

  @AfterEach
  @Transactional
  void tearDown() {
    entityManager.createNativeQuery("delete from test.task_table").executeUpdate();
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
  }

  @Test
  void shouldUpdateTaskAndReturnAccepted() {
    RestAssured.given()
      .contentType(ContentType.JSON)
      .cookie("auth_token", jwtCookie)
      .body(new CreateUpdateTaskRequest("Updated title", "Updated description", TaskStatus.IN_DEVELOPMENT))
      .when()
      .put("/api/v1/task-tracker/tasks/update/%d".formatted(createdTaskId))
      .then()
      .statusCode(202)
      .body("id", Matchers.is(createdTaskId.intValue()))
      .body("title", Matchers.equalTo("Updated title"))
      .body("description", Matchers.equalTo("Updated description"))
      .body("status", Matchers.equalTo("IN_DEVELOPMENT"))
      .body("modifiedAt", Matchers.notNullValue(LocalDateTime.class));
  }
}