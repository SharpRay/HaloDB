package org.rzlabs.halo.guice;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.rzlabs.halo.util.common.lifecycle.Lifecycle;
import org.rzlabs.halo.util.common.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A scope that adds objects to the Lifecycle.
 * This is by definition also a lazy singleton scope.
 */
public class LifecycleScope implements Scope {

    private static final Logger log = new Logger(LifecycleScope.class);
    private final Lifecycle.Stage stage;

    private Lifecycle lifecycle;
    private final List<Object> instances = new ArrayList<>();

    public LifecycleScope(Lifecycle.Stage stage) {
        this.stage = stage;
    }

    public void setLifecycle(Lifecycle lifecycle) {
        synchronized (instances) {
            this.lifecycle = lifecycle;
            instances.forEach(instance -> lifecycle.addManagedInstance(instance, stage));
        }
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            private volatile T value = null;

            @Override
            public T get() {
                if (value == null) {
                    final T retVal = unscoped.get();

                    synchronized (instances) {
                        if (lifecycle == null) {
                            instances.add(retVal);
                        } else {
                            // If lifecycle has started.
                            try {
                                lifecycle.addMaybeStartManagedInstance(retVal, stage);
                            } catch (Exception e) {
                                log.warn("Caught exception when trying to create a [%s]", key);
                                return null;
                            }
                        }
                    }
                    value = retVal;
                }
                return value;
            }
        };
    }
}
