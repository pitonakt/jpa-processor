package com.pitonak.jpa.processor.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import org.apache.commons.lang3.StringUtils;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes("javax.persistence.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_10)
@AutoService(Processor.class)
public class EntityProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            final Set<TypeElement> entities = (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(annotation);

            for (TypeElement entity : entities) {
                String className = entity.getQualifiedName() + "Processed";

                try (BufferedReader br = new BufferedReader(new FileReader(new File("Person.class")))) {
                    System.out.println(br.readLine());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    JavaFileObject jfo = processingEnv.getFiler()
                            .createSourceFile(className);
                    try (PrintWriter out = new PrintWriter(jfo.openWriter())) {
                        out.print("package ");
                        out.print(StringUtils.substringBeforeLast(className,
                                "."));
                        out.println("");
                        out.println();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        return true;
    }
}
