package com.momo.certChain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.dto.AddressDTO;
import com.momo.certChain.model.dto.CertificationDTO;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.model.dto.request.CreateInstitutionDTO;
import com.momo.certChain.services.InstitutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.crypto.CipherException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@RestController
@RequestMapping("/institution")
public class InstitutionController extends BaseController {

    private final InstitutionService institutionService;

    private final ObjectMapper objectMapper;

    public InstitutionController(InstitutionService institutionService, ObjectMapper objectMapper) {
        this.institutionService = institutionService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public InstitutionDTO createInstitution(@RequestBody CreateInstitutionDTO createInstitutionDTO) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, CipherException, MessagingException {
        AddressDTO addressDTO = createInstitutionDTO.getAddressDTO();
        InstitutionDTO institutionDTO = createInstitutionDTO.getInstitutionDTO();
        return institutionService.toDTO(institutionService.createInstitution(addressDTO.getStreet(),
                addressDTO.getCity(),
                addressDTO.getProvince(),
                addressDTO.getPostalCode(),
                addressDTO.getCountry(),
                institutionDTO.getName(),
                createInstitutionDTO.getWalletPassord(),
                institutionDTO.getUsername(),
                institutionDTO.getPassword(),
                createInstitutionDTO.getPasswordConfirmation()));
    }

    @GetMapping("/{institutionId}")
    public InstitutionDTO getInstitution(@PathVariable String institutionId) {
        return institutionService.toDTO(institutionService.getInstitution(institutionId));
    }

    @PostMapping("/uploadCertification/{institutionId}")
    public ResponseEntity uploadCertifications(@RequestParam("file") MultipartFile file,
                                               @RequestParam("walletPassword") String walletPassword,
                                               @PathVariable String institutionId,
                                               @RequestParam("campagneName") String campagneName) throws Exception {
        institutionService.uploadCertificationsToBlockChain(file.getBytes(), institutionId, walletPassword, campagneName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/createTemplate")
    public InstitutionDTO createCertificationTemplate(@RequestParam("universityLogo") MultipartFile universityLogo,
                                                      @RequestParam("universityStamp") MultipartFile universityStamp,
                                                      @RequestParam("certificationDTO") String certificationString,
                                                      @RequestParam("institutionId") String institutionId) throws IOException {
        Institution institution = institutionService.createInstitutionCertificateTemplate(institutionId,
                objectMapper.readValue(certificationString, Certification.class),
                universityLogo.getBytes(),
                universityStamp.getBytes());
        return institutionService.toDTO(institution);
    }

}
