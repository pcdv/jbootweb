package net.jflask.sun;

/**
 * Provides the mapping between a resource's path and the content type to
 * associate it with. It is used by resource handlers and the typical
 * implementation is to look at the file extension in path.
 */
public interface ContentTypeProvider {

  /**
   * Returns the content type to set in response headers when serving resource
   * at specified path.
   */
  String getContentType(String path);

}
