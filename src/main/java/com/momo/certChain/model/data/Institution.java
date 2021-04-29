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
public class Institution extends User {

    private String name;

    private String contractAddress;

    @OneToMany
    private List<Certification> certifications;

    @OneToOne
    private Certification certificationTemplate;

}
