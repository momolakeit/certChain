package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class CertificationDTO {
    private String id;

    private String program;

    private Date dateOfIssuing;
}
