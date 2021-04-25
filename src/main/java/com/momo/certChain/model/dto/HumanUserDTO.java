package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HumanUserDTO extends UserDTO {

    private InstitutionDTO institution;

}
