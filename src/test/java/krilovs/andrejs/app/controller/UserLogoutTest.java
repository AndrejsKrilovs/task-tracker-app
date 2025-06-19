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

@QuarkusTest
@TestHTTPEndpoint(UserFacade.class)
class UserLogoutTest {
  @Inject
  EntityManager entityManager;
  String jwtCookie;

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


    var loginRequest = new UserLoginRequest("username", "hashedPwd");
    var loginResponse = RestAssured.given()
      .contentType(ContentType.JSON)
      .body(loginRequest)
      .when()
      .post("/login")
      .then()
      .statusCode(200)
      .header("Set-Cookie", Matchers.containsString("auth_token"))
      .body("username", Matchers.equalTo("username"))
      .body("role", Matchers.equalTo("SOFTWARE_DEVELOPER"))
      .extract();

    jwtCookie = loginResponse.cookie("auth_token");
  }

  @AfterEach
  @Transactional
  void tearDown() {
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
  }

  @Test
  void shouldLogoutUserAndClearCookie() {
    RestAssured.given()
      .cookie("auth_token", jwtCookie)
      .when()
      .get("/logout")
      .then()
      .statusCode(200)
      .header(
        "Set-Cookie",
        Matchers.allOf(
          Matchers.containsString("auth_token="),
          Matchers.containsString("Max-Age=0")
        )
      );
  }

  @Test
  void shouldNotLogoutUserAndReturnUnauthorized() {
    RestAssured.given()
      .when()
      .get("/logout")
      .then()
      .statusCode(401);
  }
}
