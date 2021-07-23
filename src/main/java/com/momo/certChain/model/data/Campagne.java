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
public class Campagne {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    private String name;

    private Date date;

    private boolean runned;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<HumanUser> studentList;

    @ManyToOne
    private Institution institution;
}
