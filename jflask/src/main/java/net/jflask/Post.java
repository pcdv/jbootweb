package net.jflask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the associated route handler is valid for POST requests.
 * Instead of
 * <pre>
 *   &#64;Route(value="/some/path", method="POST")
 * </pre>
 * you can use
 * <pre>
 *   &#64;Post
 *   &#64;Route("/some/path")
 * </pre>
 *
 * @author pcdv
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Post {}
