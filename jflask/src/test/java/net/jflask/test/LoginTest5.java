package net.jflask.test;

import net.jflask.CustomResponse;
import net.jflask.LoginNotRequired;
import net.jflask.Route;
import org.junit.Assert;
import org.junit.Test;

/**
 * Try to reproduce bug where failed login does not prevent user from
 * accessing restricted pages. Turns out the browser (chrome) had several
 * duplicates of the session cookie and deleting one on logout was not enough
 * to disable the session (should be fixed now that cookie is set with a path).
 *
 * @author pcdv
 */
public class LoginTest5 extends AbstractAppTest {

  @LoginNotRequired
  @Route(value = "/auth/login", method = "POST")
  public CustomResponse login() {
    String login = app.getRequest().getForm("login");
    String pass = app.getRequest().getForm("password");

    if (login.equals("foo") && pass.equals("bar")) {
      app.loginUser(login);
      return app.redirect("/hello");
    }

    return app.redirectToLogin();
  }

  @Route("/hello")
  public String hello() {
    return "yo";
  }

  @Route("/login")
  public String loginPage() {
    return "Please login";
  }

  @Route("/auth/logout")
  public CustomResponse logout() {
    app.logoutUser();
    return app.redirectToLogin();
  }

  @Test
  public void testCookieWithInvalidPath() throws Exception {
    app.setRequireLoggedInByDefault(true);
    app.setLoginPage("/login");

    Assert.assertEquals("Please login", client.get("/hello"));
    Assert.assertEquals("yo",
                        client.post("/auth/login", "login=foo&password=bar"));
    Assert.assertEquals("yo", client.get("/hello"));
    Assert.assertEquals("Please login", client.get("/auth/logout"));
    Assert.assertEquals("Please login", client.get("/hello"));
  }
}
