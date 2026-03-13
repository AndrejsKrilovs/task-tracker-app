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
import krilovs.andrejs.app.entity.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

@QuarkusTest
class CreateTaskTest {
  @Inject
  EntityManager entityManager;
  String jwtCookie;

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
  }

  @AfterEach
  @Transactional
  void tearDown() {
    entityManager.createNativeQuery("delete from test.task_table").executeUpdate();
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
  }

  @Test
  void shouldCreateNewTaskAndReturnCreated() {
    var request = new CreateUpdateTaskRequest(null,"title", "description", null, "username");
    RestAssured.given()
      .contentType(ContentType.JSON)
      .cookie("auth_token", jwtCookie)
      .body(request)
      .when()
      .post("/api/v1/task-tracker/tasks/create")
      .then()
      .statusCode(201)
      .body("id", Matchers.notNullValue())
      .body("title", Matchers.equalTo("title"))
      .body("description", Matchers.equalTo("description"))
      .body("status", Matchers.equalTo("READY_FOR_DEVELOPMENT"))
      .body("createdAt", Matchers.notNullValue())
      .body("user", Matchers.equalTo("username"));
  }

  @Test
  void shouldNotCreateNewTaskAndReturnUnauthorized() {
    var request = new CreateUpdateTaskRequest(null,"title", "description", null, "test");
    RestAssured.given()
      .contentType(ContentType.JSON)
      .body(request)
      .when()
      .post("/api/v1/task-tracker/tasks/create")
      .then()
      .statusCode(401);
  }

  @Test
  void shouldNotCreateNewTaskAndReturnBadRequest() {
    var request = new CreateUpdateTaskRequest(null, null, null, null, "username");
    RestAssured.given()
      .contentType(ContentType.JSON)
      .cookie("auth_token", jwtCookie)
      .body(request)
      .when()
      .post("/api/v1/task-tracker/tasks/create")
      .then()
      .statusCode(400)
      .body("status", Matchers.equalTo("BAD_REQUEST"))
      .body("timestamp", Matchers.notNullValue())
      .body("path", Matchers.equalTo("/api/v1/task-tracker/tasks/create"))
      .body("message", Matchers.is(Map.of("createTask.request.title", "Title is required")));
  }
}