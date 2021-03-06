package com.pitonak.jpa.processor.processing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pitonak.jpa.exception.EntityProcessingException;
import com.pitonak.jpa.processor.EntityCopyProcessor;
import com.pitonak.jpa.processor.processing.model.Company;
import com.pitonak.jpa.processor.processing.model.Person;
import com.pitonak.jpa.utils.FieldUtils;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

// @formatter:off
class EntityCopyProcessorTest {

    private final PodamFactory faker = new PodamFactoryImpl();

    @Test
    @DisplayName("Should nullify identifiers of single entity")
    public void when_entity_copy_created___should_nullify_identifiers() {
        Person person = faker.manufacturePojo(Person.class);

        final Person copy = EntityCopyProcessor.copy(person);

        assertThat(copy, is(notNullValue()));
        assertThat(person.getId(), is(not(copy.getId())));
    }

    @Test
    @DisplayName("Should nullify identifiers of entity and nested entities")
    public void when_entity_copy_created___should_nullify_nested_identifiers() throws Exception {
        Company company = faker.manufacturePojo(Company.class);

        final Company copy = EntityCopyProcessor.copy(company);
        
        assertThat(copy, is(notNullValue()));
        assertThat(company.getId(), is(not(copy.getId())));
        assertThat(copy.getEmployees(), everyItem(hasProperty("id", is(nullValue()))));
    }
    
    @Test
    @DisplayName("Should throw an exception when class is missing field")
    public void when_no_field_is_found___should_throw_an_exception() {
        assertThrows(EntityProcessingException.class, () -> FieldUtils.getField(new Company(), "unknown"));
    }
}
