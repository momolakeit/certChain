package com.momo.certChain.model.dto;

import com.momo.certChain.model.data.Certification;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.List;

@Getter
@Setter
public class InstitutionDTO extends UserDTO {

    private String name;

    private String contractAddress;

    private WalletDTO walletDTO;

    private List<CertificationDTO> certifications;

}
