package org.rzlabs.halo.guice;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.spi.Message;
import org.rzlabs.halo.util.common.StringUtils;
import org.rzlabs.halo.util.common.logger.Logger;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Validator;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class JsonConfigurator {

    private static final Logger log = new Logger(JsonConfigurator.class);

    private final ObjectMapper jsonMapper;
    private final Validator validator;

    @Inject
    public JsonConfigurator(ObjectMapper jsonMapper, Validator validator) {
        this.jsonMapper = jsonMapper;
        this.validator = validator;
    }

    public <T> T configurate(Properties props, String propertyPrefix, Class<T> clazz)
            throws ProvisionException {
        return configurate(props, propertyPrefix, clazz, null);
    }

    public <T> T configurate(Properties props,
                             String propertyPrefix,
                             Class<T> clazz,
                             @Nullable Class<? extends T> defaultClass)
            throws ProvisionException {
        verifyClazzIsConfigurable(jsonMapper, clazz, defaultClass);

        // Make it end with a period so we only include properties with sub-object thingies.
        final String propertyBase = propertyPrefix.endsWith(".") ? propertyPrefix : propertyPrefix + ".";

        Map<String, Object> jsonMap = new HashMap<>();
        for (String prop : props.stringPropertyNames()) {
            if (prop.startsWith(propertyBase)) {
                final String propValue = props.getProperty(prop);
                Object value;
                try {
                    // If it's a String Jackson wnats it to be quoted,
                    // so check if it's not an object or array and quote.
                    String modifiedPropValue = propValue;
                    if (!(modifiedPropValue.startsWith("[") || modifiedPropValue.startsWith("{"))) {
                        modifiedPropValue = jsonMapper.writeValueAsString(propValue);
                    }
                    value = jsonMapper.readValue(modifiedPropValue, Object.class);
                } catch (IOException e) {
                    log.info(e, "Unable to parse [%s]=[%s] as a json object, using as is.", prop, propValue);
                    value = propValue;
                }
                hierarchicalPutValue(propertyPrefix, prop, prop.substring(propertyBase.length()), value, jsonMap);
            }
        }

        final T config;
        try {
            if (defaultClass != null && jsonMap.isEmpty()) {
                // No configs were provided. Don't use the jsonMapper; instead create a default instance of the
                // default class using the no-arg constructor. We know it exists because verifyClazzIsConfigurable
                // checks for it.
                config = defaultClass.getConstructor().newInstance();
            } else {
                config = jsonMapper.convertValue(jsonMap, clazz);
            }
        } catch (IllegalArgumentException e) {
            throw new ProvisionException(StringUtils.format("Problem parsing object at prefix[%s]: %s.",
                    propertyPrefix, e.getMessage()), e);
        } catch (NoSuchMethodException | InstantiationException |
                IllegalAccessException | InvocationTargetException e) {
            throw new ProvisionException(StringUtils.format("Problem instantiating object at prefix[%s]:" +
                    " %s: %s.", propertyPrefix, e.getClass().getSimpleName(), e.getMessage()), e);
        }

        final Set<ConstraintViolation<T>> violations = validator.validate(config);
        if (!violations.isEmpty()) {
            List<String> messages = new ArrayList<>();
            for (ConstraintViolation<T> violation : violations) {
                StringBuilder path = new StringBuilder();
                try {
                    Class<?> beanClazz = violation.getRootBeanClass();
                    final Iterator<Path.Node> iter = violation.getPropertyPath().iterator();
                    while (iter.hasNext()) {
                        Path.Node next = iter.next();
                        if (next.getKind() == ElementKind.PROPERTY) {
                            final String fieldName = next.getName();
                            final Field theField = beanClazz.getDeclaredField(fieldName);

                            if (theField.getAnnotation(JacksonInject.class) != null) {
                                path = new StringBuilder(
                                        StringUtils.format(" -- Injected field[%s] not bound!?", fieldName));
                                break;
                            }

                            JsonProperty annotation = theField.getAnnotation(JsonProperty.class);
                            final boolean noAnnotationValue =
                                    annotation == null || Strings.isNullOrEmpty(annotation.value());
                            final String pathPart = noAnnotationValue ? fieldName : annotation.value();
                            if (path.length() == 0) {
                                path.append(pathPart);
                            } else {
                                path.append(".").append(pathPart);
                            }
                        }
                    }
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }

                messages.add(StringUtils.format("%s = %s", path.toString(), violation.getMessage()));
            }

            throw new ProvisionException(
                    Iterables.transform(
                            messages,
                            new Function<String, Message>() {
                                @Nullable
                                @Override
                                public Message apply(@Nullable String input) {
                                    return new Message(StringUtils.format("%s%s", propertyBase, input));
                                }
                            }
                    )
            );
        }

        log.debug("Loaded class[%s] from props[%s] as [%s]", clazz, propertyBase, config);

        return config;
    }

    private static void hierarchicalPutValue(String propertyPrefix,
                                             String originalProperty,
                                             String property,
                                             Object value,
                                             Map<String, Object> targetMap) {
        int dotIndex = property.indexOf('.');
        // Always put property with name even if it is of form a.b.
        // This will make sure the property is available for classes where JsonProperty names are of the form a.b.
        // Note:- this will cause more the required properties to be present in the jsonMap.
        targetMap.put(property, value);
        if (dotIndex < 0) {
            return;
        }
        if (dotIndex == 0) {
            throw new ProvisionException(
                    StringUtils.format("Double dot in property: %s", originalProperty));
        }
        if (dotIndex == property.length() - 1) {
            throw new ProvisionException(
                    StringUtils.format("Dot at the end of property: %s", originalProperty));
        }

        String nestedKey = property.substring(0, dotIndex);
        Object nested = targetMap.computeIfAbsent(nestedKey, k -> new HashMap<String, Object>());
        if (!(nested instanceof Map)) {
            // Clash is possible between properties, which are used to configure different objects: e.g.
            // halo.emitter=parameterized is used to configure Emitter class,
            // and halo.emitter.parameterized.xxx=yyy is used to configure ParameterizedUriEmitterConfig
            // object. So skipping xxx=yyy key-value
            log.info("Skipping %s property: " +
                    "one of it's prefixes is also used as a property key. Prefix: %s",
                    originalProperty,
                    propertyPrefix);
            return;
        }
        Map<String, Object> nestedMap = (Map<String, Object>) nested;
        hierarchicalPutValue(propertyPrefix, originalProperty,
                property.substring(dotIndex + 1), value, nestedMap);
    }

    public static <T> void verifyClazzIsConfigurable(ObjectMapper mapper, Class<T> clazz,
                                                     @Nullable Class<? extends T> defaultClass) {
        if (defaultClass != null) {
            try {
                defaultClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new ProvisionException(StringUtils.format(
                        "JsonConfigurator requires default classes to " +
                                "have zero-arg constructors. %s doesn't",
                        defaultClass
                ));
            }
        }

        final List<BeanPropertyDefinition> beanDefs = mapper.getSerializationConfig()
                .introspect(mapper.constructType(clazz))
                .findProperties();
        for (BeanPropertyDefinition beanDef : beanDefs) {
            final AnnotatedField field = beanDef.getField();
            if (field == null || !field.hasAnnotation(JsonProperty.class)) {
                throw new ProvisionException(StringUtils.format(
                        "JsonConfigurator requires Jackson-annotated Config objects " +
                                "to have field annotations. %s doesn't",
                        clazz
                ));
            }
        }
    }
}