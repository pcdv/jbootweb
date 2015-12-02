package net.jflask.test.util;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.jflask.sun.WebServer;
import net.jflask.util.IO;

/**
 * Minimal HTTP client that mimics the behaviour of a browser, eg. by allowing
 * cookies.
 *
 * @author pcdv
 */
public class SimpleClient {

  private final String rootUrl;

  private CookieManager cookies;

  public SimpleClient(String host, int port) {
    this("http://" + host + ":" + port);
  }

  public SimpleClient(String rootUrl) {
    this.rootUrl = rootUrl;
    this.cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    // WTF: there is no non-static way to bind a CookieHandler to an
    // URLConnection!
    CookieHandler.setDefault(cookies);
  }

  public SimpleClient(WebServer ws) {
    this("http://localhost:"+ws.getPort());
  }

  /**
   * GETs data from the server at specified path.
   */
  public String get(String path) throws IOException {
    URL url = new URL(rootUrl + path);
    return new String(IO.readFully(url.openStream()));
  }

  /**
   * POSTs data on the server at specified path and return results as string.
   */
  public String post(String path,
                     String data) throws IOException, URISyntaxException {
    URL url = new URL(rootUrl + path);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setDoOutput(true);
    con.getOutputStream().write(data.toString().getBytes());
    con.getOutputStream().close();
    con.connect();

    String resp = new String(IO.readFully(con.getInputStream()));
    return resp;
  }

  public void addCookie(String name, String value) {
    HttpCookie cookie = new HttpCookie(name, value);
    cookie.setPath("/");
    cookies.getCookieStore().add(URI.create(rootUrl), cookie);
  }
}
