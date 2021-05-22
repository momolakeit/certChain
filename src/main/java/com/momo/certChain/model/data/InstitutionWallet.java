package com.momo.certChain.model.data;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class InstitutionWallet {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    @Column( length = 100000 )
    private String privateKey;

    @Column( length = 100000 )
    private String publicKey;

    @Column( length = 100000 )
    private String publicAddress;

    private String salt;
}
