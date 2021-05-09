package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HumanUserDTO extends UserDTO {
    private String prenom;
    private String nom;
    private boolean passwordResseted;

    private InstitutionDTO institution;

}
