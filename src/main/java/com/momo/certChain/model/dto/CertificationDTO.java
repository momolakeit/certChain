package com.momo.certChain.model.dto;

import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.model.data.Signature;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CertificationDTO {
    private String id;

    private String program;

    private Date dateOfIssuing;

    private ImageFileDTO universityLogo;

    private ImageFileDTO universityStamp;

    @OneToMany
    private List<SignatureDTO> signatures;
}
