// @formatter:off
package com.pitonak.jpa.utils;

import java.lang.reflect.Field;
import java.util.Objects;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldUtils {

    private FieldUtils() {}
    
    @SneakyThrows
    public static <T> T getValue(T source, Field field) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(field, "field must not be null");
        
        log.debug("getValue(source={}, field={})", source.getClass().getName(), field.getName());
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        
        @SuppressWarnings("unchecked")
        T value = (T) field.get(source);
        field.setAccessible(false);
        
        log.debug("getValue(), result: {}", value);
        return value;
    }
    
    @SneakyThrows
    public static <T> Field getField(T source, String fieldName) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(fieldName, "fieldName must not be null");
        
        log.debug("getField(source={}, fieldName={})", source.getClass().getName(), fieldName);
        final Field field = source.getClass().getDeclaredField(fieldName);
        
        log.debug("getField(), result: {}", field.getName());
        return field;
    }
    
    @SneakyThrows
    public static void setValue(Field field, Object target, Object value) {
        Objects.requireNonNull(field, "field must not be null");
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(value, "value must not be null");
        
        log.debug("setValue(field={}, target={}, value={})", field.getName(), target.getClass().getName(), value);
        
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        field.set(target, value);
        field.setAccessible(false);
        
        log.debug("setValue(), result: successfully set");
    }
}
