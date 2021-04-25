package com.momo.certChain.model.data;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Institution extends User {

    private String name;

    private String contractAddress;

}
