package com.momo.certChain.model.dto;

import com.momo.certChain.model.Type;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
public class LienDTO {
    private String id;

    private String certificateEncKey;

    private String titre;
    
    private Date dateExpiration;

    private CertificationDTO certification;

    private Type type;
}
