package com.momo.certChain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.dto.CertificationDTO;
import com.momo.certChain.model.dto.request.CreateLienDTO;
import com.momo.certChain.services.CertificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/certification")
public class CertificationController extends BaseController {

    private final CertificationService certificationService;

    private final ObjectMapper objectMapper;

    public CertificationController(CertificationService certificationService, ObjectMapper objectMapper) {
        this.certificationService = certificationService;
        this.objectMapper = objectMapper;
    }


    @GetMapping("/fetchCertificate/{id}/{key}")
    public CertificationDTO certificationDTO(@PathVariable String id, @PathVariable String key) throws Exception {
        Certification certification = certificationService.getUploadedCertification(id, key);
        return certificationService.toDTO(certification);
    }

    @PostMapping("/createLien")
    public String createLien(@RequestBody CreateLienDTO createLienDTO) throws Exception {
        return certificationService.createLien(
                createLienDTO.getCertificationId(),
                createLienDTO.certificationPassword,
                createLienDTO.getDateExpriration());
    }

    @DeleteMapping("/forgetCertificate/{certificateId}")
    public ResponseEntity forgetCertificate(@PathVariable String certificateId) {
        certificationService.forgetCertificate(certificateId);
        return ResponseEntity.ok().build();
    }
}
