package com.pitonak.jpa.processor.processing;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

//@formatter:off
@Slf4j
public class EntityCopyBuilder {
    
    public static <T> T copy(T source) {
        
        Object[] target = new Object[] {null};
        final Field[] declaredFields = source.getClass().getDeclaredFields();
        
        Try.of(() -> source.getClass().newInstance()).onSuccess(destination -> {
            final LinkedList<Field> collectionFields = new LinkedList<>();
            
            final String[] ignoreFields = Stream.of(declaredFields)
                    .map(field -> {
                        field.setAccessible(true);
                        
                        boolean instanceOfCollection[] = new boolean[] {false};
                        Try.of(() -> (field.get(source)))
                                .onSuccess(o -> instanceOfCollection[0] = (o instanceof Collection<?>));
                                
                        if (field.isAnnotationPresent(OneToMany.class) && instanceOfCollection[0]) {
                            collectionFields.add(field);
                        }
                        
                        field.setAccessible(false);
                        
                        return field;
                    })
                    .filter(field -> field.isAnnotationPresent(Id.class))
                    .map(Field::getName)
                    .toArray(String[]::new);
                
                BeanUtils.copyProperties(source, destination, ignoreFields);
                
                collectionFields.forEach(field -> {
                    field.setAccessible(true);
                    
                    Try.of(() -> (field.get(source)))
                    .onSuccess(sourceCollection -> {
                        final Set<T> collectionCopy = ((Collection<?>) sourceCollection).stream()
                                .map(obj -> {
                                    T copy = (T) copy(obj);
                                    
                                    String mappedBy = field.getAnnotation(OneToMany.class).mappedBy();
                                    Try.of(() -> (copy.getClass().getDeclaredField(mappedBy)))
                                        .onSuccess(f -> {
                                            f.setAccessible(true);
                                            ReflectionUtils.setField(f, copy, destination);
                                            f.setAccessible(false);
                                        });
                                    
                                    return copy;
                                })
                                .collect(toSet());
                                                    
                        Try.of(() -> (Collection) field.get(destination))
                            .onSuccess(collection -> {
                                collection.clear();
                                collection.addAll(collectionCopy);
                            });
                    });
                    
                    field.setAccessible(false);
                });
                
                target[0] = destination;
        });
        
        return (T) target[0];
    }
}
