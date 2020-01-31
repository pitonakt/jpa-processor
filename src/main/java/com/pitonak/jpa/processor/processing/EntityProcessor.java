package com.pitonak.jpa.processor.processing;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes("javax.persistence.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class EntityProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        annotations.stream()
            .forEach(elem -> {

                final Set<? extends Element> annotatedElems = roundEnv.getElementsAnnotatedWith(elem);
                
                annotatedElems.forEach(e -> {
                    
                    try {
                        if (e instanceof TypeElement) {
                            String className = ((TypeElement) e).getQualifiedName().toString();
                            final Class<?> clazz = Class.forName(className);
                        }
                    } catch (Exception ex) {
                        
                    }
                    
                });
            });
        
        
        return true;
    }
    
    private void writeFile() {
        
    }
}
