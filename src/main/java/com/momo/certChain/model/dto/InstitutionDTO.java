package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
public class InstitutionDTO extends UserDTO {

    private String name;

    private String contractAddress;

}
