package org.rzlabs.halo.guice;

import com.google.inject.Key;

/**
 * @author zharui
 */
public class KeyHolder<T> {

    private final Key<? extends T> key;

    public KeyHolder(Key<? extends T> key) {
        this.key = key;
    }

    public Key<? extends T> getKey() {
        return key;
    }
}
