package com.momo.certChain.model.data;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
@Getter
@Setter
public class Student extends HumanUser {
    @OneToMany
    private List<Certification> certifications;
}
