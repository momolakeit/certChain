package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
public class InstitutionWalletDTO {
    private String id;

    private String privateKey;

    private String publicKey;

    private String publicAddress;
}
