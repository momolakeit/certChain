package com.momo.certChain.controller;

import com.momo.certChain.model.data.Campagne;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.model.dto.CampagneDTO;
import com.momo.certChain.model.dto.SignatureDTO;
import com.momo.certChain.services.SignatureService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.crypto.Sign;

import java.io.IOException;

@RestController
@RequestMapping("/signature")
public class SignatureController {

    private final SignatureService signatureService;

    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
    @PostMapping("/addImage/{signatureId}")
    public SignatureDTO fetchCampagne(@PathVariable String signatureId, @RequestParam("file") MultipartFile file) throws IOException {
        Signature signature = signatureService.addSignatureImage(signatureId,file.getBytes());
        return signatureService.toDto(signature);
    }
}
