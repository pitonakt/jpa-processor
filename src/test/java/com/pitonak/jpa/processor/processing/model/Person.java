package com.pitonak.jpa.processor.processing.model;

import static javax.persistence.CascadeType.ALL;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

    private String name;

    private int age;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @JoinColumn
    @OneToOne(cascade = ALL, orphanRemoval = true)
    private Address address;
}
