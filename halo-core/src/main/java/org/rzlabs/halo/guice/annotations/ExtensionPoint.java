package org.rzlabs.halo.guice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that the annotated type is an extension point. Extension points are interfaces or non-final classes
 * that may be subclassed in extensions in order to add functionality to HaloDB. Extension points may change in
 * breaking ways only between major HaloDB lins (e.g. 0.1.x -> 0.2.0), but otherwise must remain stable. Extension
 * points may change at any time in non-breaking ways, however. such as by adding new default methods to an interface.
 *
 * All public and protected fields, methods, and constructors of annotated classes and interfaces are considered
 * stable in this sense. If a class is not annotated, but an individual field, method, or constructor is
 * annotated, then only that particular field, method, or constructor is considered an extension API.
 *
 * Extension points are all considered public APIs in the sense of {@link PublicApi}, even if not explicitly annotated
 * as such.
 *
 * Note that there are number of injectable interfaces that are not annotated with {@code ExtensionPoint}. You may
 * still extend these interface in extensions, but your extension may need to be recompiled even for a minor
 * update of HaloDB.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface ExtensionPoint {
}
