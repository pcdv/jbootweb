package net.jflask.test;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Reproduces bug in AbstractResourceHandler: the ?search= part of the URL
 * was not ignored.
 *
 * @author pcdv
 */
public class ResourceWithQueryStringTest extends AbstractAppTest {

  @Test
  public void testIt() throws Exception {
    Path dir = Files.createTempDirectory("jflask");
    Files.write(dir.resolve("foo"), "Foo".getBytes());

    app.servePath("/", dir.toString());
    assertEquals("Foo", client.get("/foo"));
    assertEquals("Foo", client.get("/foo?q=1"));
  }
}
