package org.rzlabs.halo.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.rzlabs.halo.guice.HaloScopes;
import org.rzlabs.halo.guice.annotations.LazySingleton;

public class SingletonBindingModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bindScope(LazySingleton.class, HaloScopes.SINGLETON);
    }
}
