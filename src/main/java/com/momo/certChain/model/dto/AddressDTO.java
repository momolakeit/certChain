package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
public class AddressDTO {
    private String id;

    private String street;

    private String city;

    private String province;

    private String postalCode;

    private String Country;
}
