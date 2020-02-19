// @formatter:off
package com.pitonak.jpa.processor.processing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.pitonak.jpa.processor.EntityCopyProcessor;
import com.pitonak.jpa.processor.processing.model.Address;
import com.pitonak.jpa.processor.processing.model.Company;
import com.pitonak.jpa.processor.processing.model.Person;
import com.pitonak.jpa.processor.processing.model.Role;

import uk.co.jemos.podam.api.DefaultClassInfoStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EntityCopyProcessorPersistanceTest {

    
    private static EntityManager em = Persistence.createEntityManagerFactory("hsqldb").createEntityManager();
    
    private static final PodamFactory faker = new PodamFactoryImpl();
    
    private Company company;
    private Company companyCopy;
    
    @BeforeAll
    public static void setUp() {
        em.getTransaction().begin();
        
        LongStream.rangeClosed(1, 3)
        .forEach(idx -> {
            Role role = new Role();
            em.persist(role);
        });
        
        final DefaultClassInfoStrategy classStrategy = DefaultClassInfoStrategy.getInstance();
        classStrategy.addExcludedField(Person.class,
                "company");
        classStrategy.addExcludedField(Role.class, "persons");
        classStrategy.addExcludedField(Person.class, "roles");

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
    @Order(1)
    @DisplayName("Should successfully duplicate and persist copy of company")
    public void should_successfully_persist___when_company_copy_is_generated() {                
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
    
    @Test
    @Order(2)
    @DisplayName("Should successfully copy and persist many-to-many relationship")
    public void should_succesfully_persist___when_user_roles_are_copied() {
        CriteriaQuery<Role> criteria = em.getCriteriaBuilder().createQuery(Role.class);
        criteria.select(criteria.from(Role.class));
        final Set<Role> roles = em.createQuery(criteria).getResultStream().collect(Collectors.toSet());
                
        IntStream.rangeClosed(1, 3)
            .forEach(idx -> {
                Person person = faker.manufacturePojo(Person.class);
                person.setRoles(roles);
                Person copy = EntityCopyProcessor.copy(person);
                
                em.persist(copy);
            });
        
        CriteriaQuery<Role> criteriaRole = em.getCriteriaBuilder().createQuery(Role.class);
        criteriaRole.select(criteriaRole.from(Role.class));
                
        final CriteriaBuilder criteriaBuilderPerson = em.getCriteriaBuilder();
        final CriteriaQuery<Person> criteriaQueryPerson = criteriaBuilderPerson.createQuery(Person.class);
        final Root<Person> person = criteriaQueryPerson.from(Person.class);
        criteriaQueryPerson.select(person)
            .where(criteriaBuilderPerson.isNotEmpty(person.get("roles")))
            .getSelection();
        final List<Person> personsWithRoles = em.createQuery(criteriaQueryPerson).getResultList();
        
        assertThat(em.createQuery(criteriaRole).getResultList().size(), is(3));
        assertThat(personsWithRoles.size(), is(3));
    }
    
    @AfterAll
    public static void tearDown() {
        em.getTransaction().rollback();
    }
}
