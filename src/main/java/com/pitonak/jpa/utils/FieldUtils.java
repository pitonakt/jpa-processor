// @formatter:off
package com.pitonak.jpa.utils;

import java.lang.reflect.Field;
import java.util.Objects;

import com.pitonak.jpa.exception.EntityProcessingException;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to make easier working with {@link java.lang.reflect.Field} objects.
 * 
 * @author pitonakt
 * @since 1.0.0
 */
@Slf4j
public final class FieldUtils {

    private FieldUtils() {}
    
    /**
     * Returns value of field in source class
     * 
     * @param <T> type of source object
     * @param source source object containing desired field
     * @param field field name holding value
     * 
     * @return value of source object field
     * @throws EntityProcessingException if no value can be returned
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(T source, Field field) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(field, "field must not be null");
        
        final String sourceClassName = source.getClass().getName();
        final String fieldName = field.getName();
        log.debug("getValue(source={}, field={})", sourceClassName, fieldName);
        
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        
        T value = null;
        try {
            value = (T) field.get(source);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            throw new EntityProcessingException(String.format("Cannot get value of field '%s' from class '%s'", sourceClassName, fieldName), e);
        }
        
        field.setAccessible(false);
        
        log.debug("getValue(), result: {}", value);
        return value;
    }
    
    /**
     * Returns {@link java.lang.reflect.Field} from source object 
     * 
     * @param <T> type of source object
     * @param source source object containing desired field
     * @param fieldName name of field to be returned
     * 
     * @return field from source
     * @throws EntityProcessingException if no field has been found
     */
    public static <T> Field getField(T source, String fieldName) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(fieldName, "fieldName must not be null");
        
        final String sourceClassName = source.getClass().getName();
        log.debug("getField(source={}, fieldName={})", source.getClass().getName(), fieldName);
        
        Field field = null;
        try {
            field = source.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException | SecurityException e) {
            log.error(e.getMessage(), e);
            throw new EntityProcessingException(String.format("Cannot get field '%s' from class '%s'", sourceClassName, fieldName), e);
        }
        
        log.debug("getField(), successfully found '{}' field", fieldName);
        return field;
    }
    
    /**
     * Set {@link java.lang.reflect.Field} value of target object
     * 
     * @param field field of target object to be updated
     * @param target target object holding field
     * @param value value to be set
     * 
     * @throws EntityProcessingException if field value cannot be set
     */
    public static void setValue(Field field, Object target, Object value) {
        Objects.requireNonNull(field, "field must not be null");
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(value, "value must not be null");
        
        final String fieldName = field.getName();
        final String targetClassName = target.getClass().getName();
        log.debug("setValue(field={}, target={}, value={})", fieldName, targetClassName, value);
        
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        
        try {
            field.set(target, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new EntityProcessingException(String.format("Unable to set value '%s' to field '%s' from class '%s'", value, fieldName, targetClassName), e);
        }
        
        field.setAccessible(false);
        log.debug("setValue(), result: successfully set");
    }
}
