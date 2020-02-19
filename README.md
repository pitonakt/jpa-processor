# JPA-processor
![JPA Processor CI pipeline](https://github.com/pitonakt/jpa-processor/workflows/JPA%20Processor%20CI%20pipeline/badge.svg) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pitonakt_jpa-processor&metric=alert_status)](https://sonarcloud.io/dashboard?id=pitonakt_jpa-processor)

JPA processor to help copy JPA entities.


### How to use
Checkout project at _[JPA-processor](https://github.com/pitonakt/jpa-processor.git)_. There is _EntityCopyProcessor_ class with **copy** function, which does the magic. This method will copy all the fields of your entity recursively, involving also collections with OneToMany/ManyToOne/ManyToMany relationships, nullify fields annotated with **javax.persistence.Id** annotation and set new child entities to it's newly created parent. There are no requirements for additional custom setter methods in your entity.  

### Example of usage
Create simple company entity with OneToMany relationship to **Person** entity. Note that company can have multiple employees, but employee can always relate
only to one company.

```java
@Entity
public class Company {

    @Id @GeneratedValue
    private long id;

    @OneToMany(mappedBy = "company", cascade = ALL, orphanRemoval = true)
    private Set<Person> employees = new HashSet<>();
    
    // getters and setters
```

Create **Person** entity representing company's employees.

```java
@Entity
public class Person {

    @Id @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    
    // getters and setters
```

Now you can use _copy_ function to automatically generated a copy of your desired entity.

```java
public class Test {
	public static void main(String...args) {
		// Generate company with multiple employees
		
		Company copy = EntityCopyProcessor.copy(company);
	}
}
```

Now you are able to persist your copy into database.


### Bugs and improvements
If you see any bug or improvement to be done, create a **New issue** before you submit Pull request.