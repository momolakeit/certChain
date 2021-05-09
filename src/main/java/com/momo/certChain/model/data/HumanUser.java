package com.momo.certChain.model.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class HumanUser extends User {
    private String prenom;
    private String nom;
    private boolean passwordResseted;
    @ManyToOne
    private Institution institution;
}
