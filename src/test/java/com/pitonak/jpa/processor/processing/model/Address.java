package com.pitonak.jpa.processor.processing.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String street;

    private Integer zipCode;

    private String city;

    private String country;
}
