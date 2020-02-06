// @formatter:off
package com.pitonak.jpa.utils;

import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.OneToMany;

import com.pitonak.jpa.processor.EntityCopyProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to work update Entities with {@link javax.persistence.OneToMany} relationships 
 * 
 * @author pitonakt
 * @since 1.0.0
 */
@Slf4j
public final class EntityCollectionUtils {

    private EntityCollectionUtils() {}

    /**
     * 
     * @param source
     * @param target
     * @param collection
     */
    @SuppressWarnings("unchecked")
    public static <T> void copy(T source,
                     T target,
                     Collection<Field> collection) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(collection, "collection must not be null");

        final String className = source.getClass().getName();
        if (log.isDebugEnabled()) {
            log.debug("Copy collection properties of class '{}', fields to be copied: '{}'.",
                    className, collection.stream().map(Field::getName).collect(Collectors.joining(", ")));
        }

        collection.forEach(field -> {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            final T sourceCollection = FieldUtils.getValue(source, field);
            final Set<T> collectionCopy = ((Collection<T>) sourceCollection).stream()
                    .map(obj -> {
                        final T objectCopy = EntityCopyProcessor.copy(obj);
                        final String mappedBy = field.getAnnotation(OneToMany.class).mappedBy();
                        final Field collectionField = FieldUtils.getField(objectCopy, mappedBy);
                        
                        FieldUtils.setValue(collectionField, objectCopy, target);
                        return objectCopy;
                    })
                    .collect(toSet());

            Optional.ofNullable(FieldUtils.getValue(target, field))
                .ifPresent(c -> {
                    ((Collection<T>) c).clear();
                    ((Collection<T>) c).addAll(collectionCopy);
                });

            field.setAccessible(false);
        });
        
        log.debug("All collections of class '{}' have been successfully copied.", className);
    }
}
