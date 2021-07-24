package com.momo.certChain.model.data;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Certification {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    private String program;

    private Date dateOfIssuing;

    private String certificateText;

    private String salt;

    private boolean payed;

    @ManyToOne
    private Institution institution;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Campagne campagne;

    @OneToOne
    private ImageFile universityLogo;

    @OneToOne
    private ImageFile universityStamp;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Signature> signatures;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Lien> liens;
}
