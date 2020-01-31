package com.pitonak.jpa.processor.processing;

import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pitonak.jpa.processor.processing.model.Company;
import com.pitonak.jpa.processor.processing.model.Person;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

// @formatter:off
class EntityCopyBuilderTest {

    private final PodamFactory faker = new PodamFactoryImpl();

    @Test
    @DisplayName("Should nullify identifiers of single entity")
    public void when_entity_copy_created___should_nullify_identifiers() {
        Person person = faker.manufacturePojo(Person.class);

        final Person copy = EntityCopyBuilder.copy(person);

        assertThat(copy, is(notNullValue()));
        assertThat(person.getId(), is(not(copy.getId())));
    }

    @Test
    @DisplayName("Should nullify identifiers of entity and nested entities")
    public void when_entity_copy_created___should_nullify_nested_identifiers() throws Exception {
        Company company = faker.manufacturePojo(Company.class);

        final Company copy = EntityCopyBuilder.copy(company);
        
        assertThat(copy, is(notNullValue()));
        assertThat(company.getId(), is(not(copy.getId())));
        assertThat(copy.getEmployees(), everyItem(hasProperty("id", is(nullValue()))));
    }
}
