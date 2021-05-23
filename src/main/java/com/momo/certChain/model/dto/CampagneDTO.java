package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CampagneDTO {

    private String id;

    private String name;

    private Date date;

    private List<HumanUserDTO> studentList;

    private InstitutionDTO institution;

    private List<CampagneDTO> campagnes;
}
