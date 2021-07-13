package com.momo.certChain.controller;

import com.momo.certChain.model.dto.LienDTO;
import com.momo.certChain.services.LienService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lien")
public class LienController extends BaseController {

    private final LienService lienService;

    public LienController(LienService lienService) {
        this.lienService = lienService;
    }

    @GetMapping("/findAllByCertification/{certificationId}")
    public List<LienDTO> getAllLienForCertificate(@PathVariable String certificationId) {
        return lienService.findAllLienForCertificationUtilisateur_Externe(certificationId)
                .stream()
                .map(lienService::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/findAllByCertificationProprietaire/{certificationId}")
    public List<LienDTO> getAllLienForCertificateProprietaire(@PathVariable String certificationId) {
        return lienService.findAllLienForCertificationProprietaire_Certificat(certificationId)
                .stream()
                .map(lienService::toDTO)
                .collect(Collectors.toList());
    }
}
