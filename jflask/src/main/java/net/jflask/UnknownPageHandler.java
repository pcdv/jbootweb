package net.jflask;

import java.io.IOException;

/**
 * @author pcdv
 */
public interface UnknownPageHandler {

  void handle(Request r) throws IOException;
}
