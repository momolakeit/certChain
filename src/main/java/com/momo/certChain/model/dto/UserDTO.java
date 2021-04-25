package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
public class UserDTO {
    private String id;

    private String username;
    
    private String password;

    private AddressDTO address;
}
