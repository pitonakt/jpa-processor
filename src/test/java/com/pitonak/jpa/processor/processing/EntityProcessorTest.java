package com.pitonak.jpa.processor.processing;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

import org.junit.jupiter.api.Test;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

// @formatter:off
public class EntityProcessorTest {
    
    @Test
    public void shouldExecuteProcessor() {
        
        Compilation compilation = javac()
                .withProcessors(new EntityProcessor())
                .compile(JavaFileObjects.forResource("Person.java"), JavaFileObjects.forResource("Company.java"));

        assertThat(compilation).succeededWithoutWarnings();
    }
}
