package com.pitonak.jpa.processor.processing.model;

import static javax.persistence.CascadeType.ALL;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = "company")
@ToString(exclude = "company")
@Entity
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private int age;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @JoinColumn
    @OneToOne(cascade = ALL, orphanRemoval = true)
    private Address address;

    @ManyToMany
    private Set<Role> roles;
}
