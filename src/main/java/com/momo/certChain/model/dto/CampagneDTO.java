package com.momo.certChain.model.dto;

import com.momo.certChain.model.data.Student;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

public class CampagneDTO {

    private String id;

    private String name;

    private Date date;

    private List<StudentDTO> studentList;
}
