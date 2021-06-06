package org.rzlabs.halo.guice;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the object to be managed by {@link org.rzlabs.halo.util.common.lifecycle.Lifecycle} and
 * send to be on {@link org.rzlabs.halo.util.common.lifecycle.Lifecycle.Stage#INIT} stage. This stage
 * gets defined by {@link LifecycleModule}.
 *
 * @author zharui
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ScopeAnnotation
public @interface ManageLifecycleInit {
}
