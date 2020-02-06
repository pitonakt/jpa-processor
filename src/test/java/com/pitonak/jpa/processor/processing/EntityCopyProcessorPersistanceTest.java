package com.pitonak.jpa.processor.processing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pitonak.jpa.processor.EntityCopyProcessor;
import com.pitonak.jpa.processor.processing.model.Address;
import com.pitonak.jpa.processor.processing.model.Company;
import com.pitonak.jpa.processor.processing.model.Person;

import uk.co.jemos.podam.api.DefaultClassInfoStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

// @formatter:off
class EntityCopyProcessorPersistanceTest {

    @PersistenceContext
    private static EntityManager em;
    
    private static final PodamFactory faker = new PodamFactoryImpl();
    
    private Company company;
    private Company companyCopy;
    
    @BeforeAll
    public static void setUp() {
        em = Persistence.createEntityManagerFactory("hsqldb").createEntityManager();
        em.getTransaction().begin();
        
        final DefaultClassInfoStrategy classStrategy = DefaultClassInfoStrategy.getInstance();
        classStrategy.addExcludedField(Person.class,
                "company");

        faker.setClassStrategy(classStrategy);
    }
    
    @BeforeEach
    public void initEntity() {
        company = faker.manufacturePojo(Company.class);
        company.setId(null);
        company.getEmployees()
            .stream().forEach(p -> {
                p.setId(null);
                p.setCompany(company);
                p.getAddress().setId(null);
            });
        
        companyCopy = EntityCopyProcessor.copy(company);
    }
    
    @Test
    @DisplayName("Should successfully duplicate and persist copy of company")
    public void when_company_copy_is_generated___should_successfully_persist() {                
        em.persist(company);
        em.persist(companyCopy);
        
        CriteriaQuery<Company> criteria = em.getCriteriaBuilder().createQuery(Company.class);
        criteria.select(criteria.from(Company.class));
        assertThat(em.createQuery(criteria).getResultList().size(), is(2));
        
        CriteriaQuery<Person> criteriaPerson = em.getCriteriaBuilder().createQuery(Person.class);
        criteriaPerson.select(criteriaPerson.from(Person.class));
        assertThat(em.createQuery(criteriaPerson).getResultList().size(), is(10));
        
        CriteriaQuery<Address> criteriaAddress = em.getCriteriaBuilder().createQuery(Address.class);
        criteriaAddress.select(criteriaAddress.from(Address.class));
        assertThat(em.createQuery(criteriaAddress).getResultList().size(), is(10));
    }
    
    @AfterAll
    public static void tearDown() {
        em.getTransaction().rollback();
    }
}
