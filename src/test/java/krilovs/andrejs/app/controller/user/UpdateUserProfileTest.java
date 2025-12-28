package krilovs.andrejs.app.controller.user;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import krilovs.andrejs.app.controller.UserFacade;
import krilovs.andrejs.app.dto.UserLoginRequest;
import krilovs.andrejs.app.dto.UserProfileRequest;
import krilovs.andrejs.app.dto.UserRegistrationRequest;
import krilovs.andrejs.app.entity.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(UserFacade.class)
class UpdateUserProfileTest {
  @Inject
  EntityManager entityManager;
  String jwtCookie;

  @BeforeEach
  @Transactional
  void setUp() {
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
    entityManager.createNativeQuery("delete from test.user_profile_table").executeUpdate();
    var user = new UserRegistrationRequest(
      "username", "hashedPwd", null, UserRole.PRODUCT_OWNER
    );

    RestAssured
      .given()
      .contentType(ContentType.JSON)
      .body(user)
      .post("/register")
      .then()
      .statusCode(201);

    var loginRequest = new UserLoginRequest("username", "hashedPwd");
    var loginResponse = RestAssured
      .given()
      .contentType(ContentType.JSON)
      .body(loginRequest)
      .when()
      .post("/login")
      .then()
      .statusCode(200)
      .extract();

    jwtCookie = loginResponse.cookie("auth_token");
  }

  @AfterEach
  @Transactional
  void tearDown() {
    entityManager.createNativeQuery("delete from test.user_profile_table").executeUpdate();
    entityManager.createNativeQuery("delete from test.user_table").executeUpdate();
  }

  @Test
  void shouldUpdateUserProfile() {
    var request = new UserProfileRequest(
      "username",
      "Name",
      "Surname",
      "hashedPwd",
      "updated@email.com",
      UserRole.SOFTWARE_DEVELOPER
    );

    RestAssured
      .given()
      .contentType(ContentType.JSON)
      .cookie("auth_token", jwtCookie)
      .body(request)
      .when()
      .put("/profile")
      .then()
      .statusCode(202)
      .body("username", Matchers.equalTo("username"))
      .body("email", Matchers.equalTo("updated@email.com"))
      .body("role", Matchers.equalTo("SOFTWARE_DEVELOPER"))
      .body("name", Matchers.equalTo("Name"))
      .body("surname", Matchers.equalTo("Surname"));
  }
}