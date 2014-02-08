package net.jflask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Route annotation binds a method with a route so that it is automatically
 * invoked when a request is submitted with a compatible URI and HTTP method.
 * <p>
 * Notes:
 * <ul>
 * <li>As of v0.3, decorated methods must return either <code>String</code> or
 * <code>byte[]</code>.
 * <li>to register decorated methods into the App, {@link App#scan(Object)}
 * must be called with an instance of the class containing the method. This
 * step is not necessary if the method exists in a class extending
 * <code>App</code>.
 *
 * @author pcdv
 * @see App
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {

  /**
   * Specifies the route. Format can be:
   * <p>
   * <b>static</b>
   *
   * <pre>
   * &#064;Route(&quot;/foo/bar&quot;)
   * public void fooBar() {
   *   return &quot;...&quot;;
   * }
   * </pre>
   *
   * <b>variable</b>
   *
   * <pre>
   * &#064;Route(&quot;/hello/:name&quot;)
   * public void hello(String name) {
   *   return &quot;Hello &quot; + name;
   * }
   * </pre>
   *
   * <b>ending with a "splat"</b>
   *
   * <pre>
   * &#064;Route(&quot;/file/*path&quot;)
   * public byte[] getFile(String path) throws IOException {
   *   if (path.contains(&quot;..&quot;))
   *     throw new IllegalArgumentException(&quot;Invalid path&quot;);
   *   return Files.readAllBytes(root.resolve(path));
   * }
   * </pre>
   */
  String value();

  /**
   * Specifies the HTTP method. Defaults to "GET".
   */
  String method() default "GET";

  /**
   * Specifies how the value returned by method must be converted and written
   * to the response stream. The given value must correspond to the name of a
   * converter registered in the App.
   */
  String converter() default "";
}
