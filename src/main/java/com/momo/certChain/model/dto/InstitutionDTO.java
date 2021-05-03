package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InstitutionDTO extends UserDTO {

    private String name;

    private String contractAddress;

    private InstitutionWalletDTO institutionWalletDTO;

    private List<CertificationDTO> certifications;

}
