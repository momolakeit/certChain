package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InstitutionDTO extends UserDTO {

    private String name;

    private String contractAddress;

    private boolean approuved;

    private List<CertificationDTO> certifications;

    private CertificationDTO certificationTemplate;

    private List<CampagneDTO> campagnes;

    private InstitutionWalletDTO institutionWallet;

}
