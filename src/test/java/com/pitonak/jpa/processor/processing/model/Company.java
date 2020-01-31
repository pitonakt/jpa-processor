package com.pitonak.jpa.processor.processing.model;

import static javax.persistence.CascadeType.ALL;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
public class Company {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "company", cascade = ALL, orphanRemoval = true)
    @Setter(value = AccessLevel.NONE)
    private Set<Person> employees = new HashSet<>();
}
