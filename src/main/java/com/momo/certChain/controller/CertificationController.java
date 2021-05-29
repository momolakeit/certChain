package com.momo.certChain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.dto.CertificationDTO;
import com.momo.certChain.model.dto.request.CreateLienDTO;
import com.momo.certChain.services.CertificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


    @GetMapping("/fetchCertificate/{certificateId}/{lienId}/{key}")
    public CertificationDTO certificationDTO(@PathVariable String certificateId,@PathVariable String lienId, @PathVariable String key) throws Exception {
        Certification certification = certificationService.getUploadedCertification(certificateId, key,lienId);
        return certificationService.toDTO(certification);
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping("/createLien")
    public String createLien(@RequestBody CreateLienDTO createLienDTO) throws Exception {
        return certificationService.createLien(
                createLienDTO.getCertificationId(),
                createLienDTO.certificationPassword,
                createLienDTO.getDateExpiration());
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @DeleteMapping("/forgetCertificate/{certificateId}")
    public ResponseEntity forgetCertificate(@PathVariable String certificateId) {
        certificationService.forgetCertificate(certificateId);
        return ResponseEntity.ok().build();
    }
}
