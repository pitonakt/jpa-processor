//@formatter:off
package com.pitonak.jpa.processor;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.beans.BeanUtils;

import com.pitonak.jpa.exception.EntityProcessingException;
import com.pitonak.jpa.utils.EntityCollectionUtils;
import com.pitonak.jpa.utils.FieldUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Processor to boilerplate setter/getter when copying entities. Was done to avoid detached entities persistence exceptions.
 * 
 * @author pitonakt
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("unchecked")
public final class EntityCopyProcessor {
    
    private EntityCopyProcessor() {}
    
    /**
     * Returns copied source entity with all relationships inside entity updated
     * 
     * @param source source to be copied
     * @return copied object of same type
     */
    public static <T> T copy(T source) {
        Objects.requireNonNull(source, "source must not be null");
        
        final Class<? extends Object> clazz = source.getClass();
        final String className = clazz.getName();
        log.debug("Creating copy of class '{}' with value '{}'", className, source);
        
        T destination;
        try {
            destination = (T) clazz.newInstance();
            final LinkedList<Field> collectionFields = new LinkedList<>();
            final List<String> ignoreFields = new LinkedList<>();
            
            Stream.of(clazz.getDeclaredFields())
                    .map(field -> {
                        final T fieldValue = FieldUtils.getValue(source, field);
                        
                        if (field.isAnnotationPresent(OneToMany.class) && fieldValue instanceof Collection<?>) {
                            collectionFields.add(field);
                        }
                        
                        if (field.isAnnotationPresent(OneToOne.class)) {
                            T copy = copy(fieldValue);
                            
                            FieldUtils.setValue(field, destination, copy);
                            ignoreFields.add(field.getName());
                        }
                        
                        return field;
                    })
                    .filter(field -> field.isAnnotationPresent(Id.class))
                    .map(Field::getName)
                    .forEach(ignoreFields::add);
                
                BeanUtils.copyProperties(source, destination, ignoreFields.toArray(new String[ignoreFields.size()]));
                EntityCollectionUtils.copy(source, destination, collectionFields);
                
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new EntityProcessingException(String.format("Cannot create new instance of class '%s'. Are you missing default constructor?", className), e);
        }
        
        log.debug("Copy of class '{}' successfully created with value '{}'", className, destination);
        return destination;
    }
}
