package org.rzlabs.halo.guice;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Scopes;

public class HaloScopes {

    public static final Scope SINGLETON = new Scope() {
        @Override
        public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
            return Scopes.SINGLETON.scope(key, unscoped);
        }

        @Override
        public String toString() {
            return "HaloScopes.SINGLETON";
        }
    };
}
