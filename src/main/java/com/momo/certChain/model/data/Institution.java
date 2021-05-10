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

    private boolean approuved;

    @OneToOne
    private Certification certificationTemplate;

    @OneToMany
    private List<Campagne> campagnes;

    @OneToOne
    private InstitutionWallet institutionWallet;

}
