package com.momo.certChain.model.data;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Lien {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    //this value is encrypted
    private String certificateEncKey;

    private String titre;

    private Date dateExpiration;

    private String salt;

    @ManyToOne
    private Certification certification;

    @Enumerated(EnumType.STRING)
    private Type type;


    public enum Type {
        UTILISATEUR_EXTERNE, PROPRIETAIRE_CERTIFICAT;
    }

}
