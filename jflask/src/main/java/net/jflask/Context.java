package net.jflask;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.jflask.util.Log;

/**
 * A HTTP handler that receives all requests on a given rootURI and dispatches
 * them to configured route handlers.
 *
 * @author pcdv
 */
public class Context implements HttpHandler, RequestHandler {

  private static final String[] EMPTY = {};

  private final String rootURI;

  private final List<MethodHandler> handlers = new ArrayList<>();

  final App app;

  public Context(App app, String rootURI) {
    this.app = app;
    this.rootURI = rootURI;
  }

  /**
   * Registers a java method that must be called to process requests matching
   * specified URI (relative to rootURI).
   *
   * @param uri URI schema relative to rootURI (eg. "/:name")
   * @param m a java method
   * @param obj the object on which the method must be invoked
   */
  public MethodHandler addHandler(String uri,
                                  Route route,
                                  Method m,
                                  Object obj) {
    Log.debug("Add handler for " + route.method() + " on " + rootURI + uri);
    MethodHandler handler = new MethodHandler(this, uri, m, obj);
    handlers.add(handler);
    return handler;
  }

  public String getRootURI() {
    return rootURI;
  }

  public void handle(HttpExchange r) throws IOException {
    SunRequest req = new SunRequest(r);
    app.setThreadLocalRequest(req);
    String uri = makeRelativeURI(req.getRequestURI());
    try {
      String[] tok = (uri.isEmpty() || uri.equals("/")) ? EMPTY
                                                        : trimLeftSlash(uri).split(
                                                            "/");
      for (MethodHandler h : handlers) {
        if (h.handle(r, tok, req)) {
          return;
        }
      }
      Log.warn("No handler found for: " + r.getRequestMethod() + " " +
               req.getRequestURI());

      app.fireError(404, req, null);

      r.sendResponseHeaders(404, 0);
    }
    catch (Throwable t) {

      if (t instanceof InvocationTargetException)
        t = ((InvocationTargetException) t).getTargetException();

      app.fireError(500, req, t);
      Log.error(t, t);
      r.sendResponseHeaders(500, 0);
      if (app.isDebugEnabled()) {
        t.printStackTrace(new PrintStream(r.getResponseBody()));
      }
    } finally {
      r.getResponseBody().close();
    }
  }

  private String makeRelativeURI(String uri) {
    if (rootURI.endsWith("/"))
      return uri.substring(rootURI.length() - 1);
    else
      return uri.substring(rootURI.length());
  }

  private String trimLeftSlash(String uri) {
    if (uri.startsWith("/"))
      return uri.substring(1);
    else
      return uri;
  }

  public void dumpUrls(StringBuilder b) {
    b.append(rootURI).append(":\n");

    ArrayList<MethodHandler> list = new ArrayList<>(handlers);
    Collections.sort(list);

    for (MethodHandler mh : list) {
      b.append(String.format("%-50s  %-8s  %-15s %s\n",
                             rootURI + mh.getURI(),
                             mh.getVerb(),
                             mh.getMethod().getName(),
                             mh.getMethod().getDeclaringClass().getName()));
    }
  }

  @Override
  public App getApp() {
    return app;
  }

  @Override
  public HttpHandler asHttpHandler() {
    return this;
  }
}
