package org.rzlabs.halo.guice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.rzlabs.halo.guice.annotations.LazySingleton;
import org.rzlabs.halo.jackson.DefaultObjectMapper;

public class JacksonModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ObjectMapper.class).to(Key.get(ObjectMapper.class));
    }

    @Provides
    @LazySingleton
    public ObjectMapper jsonMapper() {
        return new DefaultObjectMapper();
    }

    @Provides
    @LazySingleton
    public ObjectMapper jsonMapperOnlyNonNullValue() {
        return new DefaultObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
