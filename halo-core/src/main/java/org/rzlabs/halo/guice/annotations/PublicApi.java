package org.rzlabs.halo.guice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that the annotated entity is a public API for extension authors. Public APIs may change in breaking ways
 * only between major HaloDB release lines (e.g., 0.1.x -> 0.2.0), but otherwise remain stable. Public APIs may change
 * at any time in non-breaking ways, however, such as by adding new fields, methods, or constructors.
 *
 * Not that interfaces annotated with {@code PublicApi} but not with {@link ExtensionPoint} are not meant to be
 * subclassed in extensions. In this case, the annotation simply signifies that the interface is stable for callers.
 * In particular, since it is not meant to be subclassed, new non-default methods may be added to an interface and
 * new abstract matheds may be added to a class.
 *
 * If a class or interface is annotated, then all public and protected fields, methods, and constructors that class
 * or interface are considered stable in this sense. If a class is not annotated, but an individual field, method, or
 * constructor is annotated, then only that particular field, method, or constructor is considered a public API.
 *
 * Classes, fields, method, and constructors *not* annotated with {@code @PublicApi} may be modified or removed
 * in any HaloDB release, unless they are annotated with {@link ExtensionPoint} (which implies they a a public API
 * as well).
 *
 * @see ExtensionPoint
 * @see UnstableApi
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface PublicApi {
}
