//@formatter:off
package com.pitonak.jpa.processor;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Stream;

import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import com.pitonak.jpa.utils.EntityCollectionUtils;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityProcessor {
    
    private EntityProcessor() {}
    
    public static <T> T copy(T source) {
        
        Object[] target = new Object[] {null};
        final Field[] declaredFields = source.getClass().getDeclaredFields();
        
        Try.of(() -> source.getClass().newInstance()).onSuccess(destination -> {
            final LinkedList<Field> collectionFields = new LinkedList<>();
            
            final String[] processed = new String[] {null};
            final String[] ignoreFields = Stream.of(declaredFields)
                    .map(field -> {
                        field.setAccessible(true);
                        
                        boolean instanceOfCollection[] = new boolean[] {false};
                        Try.of(() -> (field.get(source)))
                                .onSuccess(o -> instanceOfCollection[0] = (o instanceof Collection<?>));
                                
                        if (field.isAnnotationPresent(OneToMany.class) && instanceOfCollection[0]) {
                            collectionFields.add(field);
                        }
                        
                        if (field.isAnnotationPresent(OneToOne.class)) {
                            Try.of(() -> copy(field.get(source)))
                                .onSuccess(value -> {
                                    ReflectionUtils.setField(field, destination, value);
                                    processed[0] = field.getName();
                                });
                        }
                        
                        field.setAccessible(false);
                        
                        return field;
                    })
                    .filter(field -> field.isAnnotationPresent(Id.class))
                    .map(Field::getName)
                    .toArray(String[]::new);
                
                BeanUtils.copyProperties(source, destination, ArrayUtils.addAll(ignoreFields, processed));
                EntityCollectionUtils.copy(source, destination, collectionFields);
                                
                target[0] = destination;
        });
        
        return (T) target[0];
    }
}
