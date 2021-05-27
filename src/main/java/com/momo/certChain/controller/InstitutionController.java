package com.momo.certChain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.dto.AddressDTO;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.model.dto.request.CreateInstitutionDTO;
import com.momo.certChain.services.InstitutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.crypto.CipherException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/institution")
public class InstitutionController extends BaseController {

    private final InstitutionService institutionService;

    private final ObjectMapper objectMapper;

    public InstitutionController(InstitutionService institutionService, ObjectMapper objectMapper) {
        this.institutionService = institutionService;
        this.objectMapper = objectMapper;
    }

    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
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

    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
    @GetMapping("/{institutionId}")
    public InstitutionDTO getInstitution(@PathVariable String institutionId) {
        return institutionService.toDTO(institutionService.getInstitution(institutionId));
    }

    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
    @PostMapping("/prepareCampagne/{institutionId}")
    public ResponseEntity prepareCampagne(@RequestParam("file") MultipartFile file,
                                          @RequestParam("walletPassword") String walletPassword,
                                          @PathVariable String institutionId,
                                          @RequestParam("campagneName") String campagneName) throws Exception {
        institutionService.prepareCampagne(file.getBytes(), institutionId, walletPassword, campagneName);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
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

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/approuveInstitution/{institutionId}")
    public InstitutionDTO approuveInstitution(@PathVariable String institutionId) {
        return institutionService.toDTO(institutionService.approveInstitution(institutionId));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/getNonApprouvedInstitutions")
    public List<InstitutionDTO> findNonApprouvedInstitutions() {
        return institutionService.findNonApprouvedInstitutions().stream()
                .map(institutionService::toDTO)
                .collect(Collectors.toList());
    }
}
