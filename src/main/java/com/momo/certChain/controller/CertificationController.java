package com.momo.certChain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.dto.CertificationDTO;
import com.momo.certChain.services.CertificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/certification")
public class CertificationController extends BaseController {

    private final CertificationService certificationService;

    private final ObjectMapper objectMapper;

    public CertificationController(CertificationService certificationService, ObjectMapper objectMapper) {
        this.certificationService = certificationService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/createTemplate")
    public CertificationDTO createCertificationTemplate(@RequestParam("universityLogo") MultipartFile universityLogo,
                                             @RequestParam("universityStamp") MultipartFile universityStamp,
                                             @RequestParam("certificationDTO") String certificationString) throws IOException {
        Certification certification = certificationService.createCertificationTemplate(objectMapper.readValue(certificationString,Certification.class),
                                                                                       universityLogo.getBytes(),
                                                                                       universityStamp.getBytes());
        return certificationService.toDTO(certification);
    }

    @GetMapping("/fetchCertificate/{id}/{key}")
    public CertificationDTO certificationDTO(@PathVariable String id,@PathVariable String key) throws Exception {
        Certification certification = certificationService.getUploadedCertification(id,key);
        return certificationService.toDTO(certification);
    }

    @DeleteMapping("/forgetCertificate/{certificateId}")
    public ResponseEntity forgetCertificate(@PathVariable String certificateId){
        certificationService.forgetCertificate(certificateId);
        return ResponseEntity.ok().build();
    }
}
