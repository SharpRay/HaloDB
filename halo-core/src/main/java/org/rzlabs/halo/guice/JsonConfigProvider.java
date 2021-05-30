package org.rzlabs.halo.guice;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.util.Types;
import org.rzlabs.halo.guice.annotations.LazySingleton;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Properties;

/**
 * Provides a singleton value of type {@code <T> from {@code Properties} bound in guice.
 * <br/>
 * <h3>Usage</h3>
 * To install this provider, bind it in your guice module, like below.
 *
 * <pre>
 * JsonConfigProvider.bind(binder, "halo.server", HaloServerConfig.class);
 * </pre>
 * <br/>
 * In the above case {@code halo.server} should be a key found in the {#code Properties} bound elsewhere.
 * The value of that key should directly relate to the fields in {@code HaloServerConfig.class}.
 *
 * <h3>Implementation</h3>
 * <br/>
 * The state of {@code <T>} is defined by the value of the property {@code propertyBase}.
 * THis value is a json structure, decoded via {@link JsonConfigurator#configurate(Properties, String, Class)}.
 * <br/>
 *
 * An excample might be if HaloServerConfig.class were
 *
 * <pre>
 *   public class HaloServerConfig {
 *     @JsonProperty @NotNull public String hostname = null;
 *     @JsonProperty @Min(1025) public int port = 8080;
 *   }
 * </pre>
 *
 * And your Properties object hat in it
 *
 * <pre>
 *   halo.server.hostname=0.0.0.0
 *   halo.server.port=8888
 * </pre>
 *
 * Then this would bind a singleton instance of a HaloServerConfig object with hpstname = "0.0.0.0" and port = 8888.
 *
 * If the port weren't set in the properties, then the default of 8080 would be taken. Essentially, it is the same as
 * substracting the "halo.server" prefix from the properties and building a Map which is then passed into
 * ObjectMapper.convertValue().
 *
 * @param <T> type of config object to provide.
 */
public class JsonConfigProvider<T> implements Provider<Supplier<T>> {

    public static <T> void bind(Binder binder, String propertyBase, Class<T> classToProvide) {
        bind(binder, propertyBase, classToProvide, Key.get(classToProvide),
                (Key) Key.get(Types.newParameterizedType(Supplier.class, classToProvide))
        );
    }

    public static <T> void bindWithDefault(Binder binder, String propertyBase, Class<T> classToProvide,
                                           Class<? extends T> defaultClass) {
        bind(binder, propertyBase, classToProvide, defaultClass, Key.get(classToProvide),
                (Key) Key.get(Types.newParameterizedType(Supplier.class, classToProvide)));
    }

    public static <T> void bind(Binder binder, String propertyBase, Class<T> classToProvide,
                                Annotation annotation) {
        bind(binder, propertyBase, classToProvide, Key.get(classToProvide, annotation),
                (Key) Key.get(Types.newParameterizedType(Supplier.class, classToProvide), annotation));
    }

    public static <T> void bind(Binder binder, String propertyBase, Class<T> classToProvide,
                                Class<? extends Annotation> annotation) {
        bind(binder, propertyBase, classToProvide, Key.get(classToProvide, annotation),
                (Key) Key.get(Types.newParameterizedType(Supplier.class, classToProvide), annotation));
    }

    public static <T> void bind(
            Binder binder,
            String propertyBase,
            Class<T> clazz,
            Key<T> instanceKey,
            Key<Supplier<T>> supplierKey
    ) {
       binder.bind(supplierKey).toProvider(of(propertyBase, clazz)).in(LazySingleton.class);
       binder.bind(instanceKey).toProvider(new SupplierProvider<>(supplierKey));
    }

    public static <T> void bind(
            Binder binder,
            String propertyBase,
            Class<T> clazz,
            Class<? extends T> defaultClass,
            Key<T> instanceKey,
            Key<Supplier<T>> supplierKey
    ) {
        binder.bind(supplierKey).toProvider(of(propertyBase, clazz, defaultClass)).in(LazySingleton.class);
        binder.bind(instanceKey).toProvider(new SupplierProvider<>(supplierKey));
    }

    public static  <T> void bindInstance(Binder binder, Key<T> bindKey, T instance) {
        binder.bind(bindKey).toInstance(instance);

        final ParameterizedType supType = Types.newParameterizedType(Supplier.class,
                bindKey.getTypeLiteral().getType());
        final Key supplierKey;

        if (bindKey.getAnnotationType() != null) {
            supplierKey = Key.get(supType, bindKey.getAnnotationType());
        } else if (bindKey.getAnnotation() != null) {
            supplierKey = Key.get(supType, bindKey.getAnnotation());
        } else {
            supplierKey = Key.get(supType);
        }

        binder.bind(supplierKey).toInstance(Suppliers.ofInstance(instance));
    }

    public static <T> JsonConfigProvider<T> of(String propertyBase, Class<T> classToProvide) {
        return of(propertyBase, classToProvide, null);
    }

    public static <T> JsonConfigProvider<T> of(
            String propertyBase,
            Class<T> classToProvide,
            Class<? extends T> defaultClass
    ) {
        return new JsonConfigProvider<>(propertyBase, classToProvide, defaultClass);
    }

    private final String propertyBase;
    private final Class<T> classToProvide;
    private final Class<? extends T> defaultClass;

    private Properties props;
    private JsonConfigurator configurator;

    private Supplier<T> retVal = null;

    public JsonConfigProvider(
            String propertyBase,
            Class<T> classToProvide,
            @Nullable Class<? extends T> defaultClass
    ) {
        this.propertyBase = propertyBase;
        this.classToProvide = classToProvide;
        this.defaultClass = defaultClass;
    }

    @Inject
    public void inject(Properties props, JsonConfigurator configurator) {
        this.props = props;
        this.configurator = configurator;
    }

    @Override
    public Supplier<T> get() {
        if (retVal != null) {
            return retVal;
        }

        final T config = configurator.configurate(props, propertyBase, classToProvide, defaultClass);
        return Suppliers.ofInstance(config);
    }
}
