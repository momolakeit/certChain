package com.momo.certChain.controller;

import com.momo.certChain.mapping.CreatedLienMapper;
import com.momo.certChain.model.CreatedLien;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.dto.CertificationDTO;
import com.momo.certChain.model.dto.request.CreateLienDTO;
import com.momo.certChain.model.dto.request.CreatePropriaitaireLienDTO;
import com.momo.certChain.model.dto.response.CreatedLienDTO;
import com.momo.certChain.services.CertificationService;
import com.sun.mail.iap.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@RestController
@RequestMapping("/certification")
public class CertificationController extends BaseController {

    private final CertificationService certificationService;


    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }


    @GetMapping("/fetchCertificate/{certificateId}/{lienId}/{key}")
    public CertificationDTO getUploadedCertificationWithLien(@PathVariable String certificateId, @PathVariable String lienId, @PathVariable String key) throws Exception {
        Certification certification = certificationService.getUploadedCertificationWithLien(certificateId, key, lienId);
        return certificationService.toDTO(certification);
    }

    //endpoint pas utiliser mais garder quand meme en cas de rollback de stratégie par rapport au lien
    @GetMapping("/fetchCertificate/{certificateId}/{key}")
    public CertificationDTO getUploadedCertificationWithPrivateKey(@PathVariable String certificateId, @PathVariable String key) throws Exception {
        Certification certification = certificationService.getUploadedCertificationWithPrivateKey(certificateId, key);
        return certificationService.toDTO(certification);
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping("/createLien")
    public CreatedLienDTO createLien(@RequestBody CreateLienDTO createLienDTO) throws Exception {
        CreatedLien createdLien = certificationService.createLien(
                createLienDTO.getCertificationId(),
                createLienDTO.getCertificationPassword(),
                createLienDTO.getTitre(),
                createLienDTO.getDateExpiration());

        return CreatedLienMapper.instance.toDTO(createdLien);
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PutMapping("/payCertificate/{certId}")
    public CertificationDTO payCertificate(@PathVariable String certId){
        Certification certification = certificationService.payCertificate(certId);

        return certificationService.toDTO(certification);
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping("/createPropriaitaireLien")
    public ResponseEntity createPropriaitaireLien(@RequestBody CreatePropriaitaireLienDTO createPropriaitaireLienDTO) throws ParseException {
        certificationService.createPropriaitaireLien(createPropriaitaireLienDTO.getCertificationId(),createPropriaitaireLienDTO.getCertificationPassword(),createPropriaitaireLienDTO.getCertEncKey());

        return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @DeleteMapping("/forgetCertificate/{certificateId}")
    public ResponseEntity forgetCertificate(@PathVariable String certificateId) {
        certificationService.forgetCertificate(certificateId);
        return ResponseEntity.ok().build();
    }
}
