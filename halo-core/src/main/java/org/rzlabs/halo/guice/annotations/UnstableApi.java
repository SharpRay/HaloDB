package org.rzlabs.halo.guice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that the annotated entity is an unstable API for extension authors. Unstable APIs may change at any time
 * in breaking ways even between minor HaloDB release lines (e.g., 0.1.0 -> 0.1.1).
 *
 * All public and protected fields, methods, and constructors of annotated classes and interfaces are considered
 * unstable in this sense.
 *
 * Unstable APIs can become {@link PublicApi}s or {@link ExtensionPoint}s once they settle down. This change can happen
 * only between major HaloDB release lins (e.g., 0.1.0 -> 0.2.0).
 *
 * @see PublicApi
 * @see ExtensionPoint
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface UnstableApi {
}
