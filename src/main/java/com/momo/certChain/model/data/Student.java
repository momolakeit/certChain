package com.momo.certChain.model.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
public class Student extends HumanUser {
    @OneToMany(cascade = CascadeType.PERSIST,orphanRemoval = true)
    private List<Certification> certifications;
}
