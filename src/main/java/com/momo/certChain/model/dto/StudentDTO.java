package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
public class StudentDTO extends HumanUserDTO {
    @OneToMany
    private List<CertificationDTO> certifications;
}
