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
import krilovs.andrejs.app.service.PasswordService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ShowAvailableUsersForTaskTest {
  @Inject
  EntityManager entityManager;

  @Inject
  PasswordService passwordService;
  String jwtCookie;

  @BeforeEach
  @Transactional
  void setUp() {
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
    entityManager.createNativeQuery("insert into test.user_table (ut_username, ut_password, ut_role) values (?1, ?2, ?3)")
                 .setParameter(1, "user_1")
                 .setParameter(2, passwordService.hashPassword("password"))
                 .setParameter(3, UserRole.SOFTWARE_DEVELOPER.name())
                 .executeUpdate();
    entityManager.createNativeQuery("insert into test.user_table (ut_username, ut_password, ut_role) values (?1, ?2, ?3)")
                 .setParameter(1, "user_2")
                 .setParameter(2, passwordService.hashPassword("password"))
                 .setParameter(3, UserRole.QA_SPECIALIST.name())
                 .executeUpdate();
    entityManager.createNativeQuery("insert into test.user_table (ut_username, ut_password, ut_role) values (?1, ?2, ?3)")
                 .setParameter(1, "user_3")
                 .setParameter(2, passwordService.hashPassword("password"))
                 .setParameter(3, UserRole.PRODUCT_OWNER.name())
                 .executeUpdate();

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
  void shouldShowUsersReturnOk() {
    RestAssured.given()
      .cookie("auth_token", jwtCookie)
      .when()
      .get("/api/v1/task-tracker/tasks/availableUsersToTask")
      .then()
      .statusCode(200)
      .body("users", Matchers.notNullValue())
      .body("users", Matchers.hasSize(4));
  }

  @Test
  void shouldShowUsersWithTaskStatusInDevelopmentReturnOk() {
    RestAssured.given()
      .cookie("auth_token", jwtCookie)
      .contentType(ContentType.JSON)
      .when()
      .get("/api/v1/task-tracker/tasks/availableUsersToTask?taskStatus=IN_DEVELOPMENT")
      .then()
      .statusCode(200)
      .body("users", Matchers.notNullValue())
      .body("users", Matchers.hasSize(1));
  }
}