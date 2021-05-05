package com.momo.certChain.controller;

import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.dto.CertificationDTO;
import com.momo.certChain.services.CertificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/institution")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @PostMapping("/createTemplate")
    public CertificationDTO certificationDTO(@RequestBody CertificationDTO certificationDTO){
        Certification certification = certificationService.createCertificationTemplate(CertificationMapper.instance.toEntity(certificationDTO));
        return certificationService.toDTO(certification);
    }

}
