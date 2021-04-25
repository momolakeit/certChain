package com.momo.certChain.model.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class HumanUser extends User {
    @ManyToOne
    private Institution institution;
}
