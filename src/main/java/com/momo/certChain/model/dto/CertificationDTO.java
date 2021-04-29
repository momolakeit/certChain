package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CertificationDTO {
    private String id;

    private String program;

    private Date dateOfIssuing;

    private InstitutionDTO institution;

    private StudentDTO student;

    private ImageFileDTO universityLogo;

    private ImageFileDTO universityStamp;

    private List<SignatureDTO> signatures;
}
