package com.momo.certChain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.certChain.model.data.Campagne;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.dto.AddressDTO;
import com.momo.certChain.model.dto.CampagneDTO;
import com.momo.certChain.model.dto.CertificationDTO;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.model.dto.request.CreateInstitutionDTO;
import com.momo.certChain.model.dto.request.UploadBlockChainContractDTO;
import com.momo.certChain.model.dto.response.JWTResponse;
import com.momo.certChain.services.CampagneService;
import com.momo.certChain.services.CertificationService;
import com.momo.certChain.services.InstitutionService;
import com.momo.certChain.services.authentification.AuthService;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/institution")
public class InstitutionController extends BaseController {

    private final InstitutionService institutionService;

    private final CampagneService campagneService;

    private final CertificationService certificationService;

    private final AuthService authService;

    private final ObjectMapper objectMapper;

    public InstitutionController(InstitutionService institutionService, CampagneService campagneService, CertificationService certificationService, AuthService authService, ObjectMapper objectMapper) {
        this.institutionService = institutionService;
        this.campagneService = campagneService;
        this.certificationService = certificationService;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public JWTResponse createInstitution(@RequestBody CreateInstitutionDTO createInstitutionDTO) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, CipherException, MessagingException {
        AddressDTO addressDTO = createInstitutionDTO.getAddressDTO();
        InstitutionDTO institutionDTO = createInstitutionDTO.getInstitutionDTO();


        Institution institution = institutionService.createInstitution(addressDTO.getStreet(),
                addressDTO.getCity(),
                addressDTO.getProvince(),
                addressDTO.getPostalCode(),
                addressDTO.getCountry(),
                institutionDTO.getName(),
                createInstitutionDTO.getWalletPassword(),
                institutionDTO.getUsername(),
                institutionDTO.getPassword(),
                createInstitutionDTO.getPasswordConfirmation());

        return authService.logInUser(institution.getUsername(),createInstitutionDTO.getInstitutionDTO().getPassword());
    }

    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
    @GetMapping("/{institutionId}")
    public InstitutionDTO getInstitution(@PathVariable String institutionId) {
        return institutionService.toDTO(institutionService.getInstitution(institutionId));
    }

    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
    @PostMapping("/prepareCampagne/{institutionId}")
    public CampagneDTO prepareCampagne(@RequestParam("file") MultipartFile file,
                                       @PathVariable String institutionId,
                                       @RequestParam("campagneName") String campagneName,
                                       @RequestParam("date") String date) throws Exception {
        Campagne campagne = institutionService.prepareCampagne(file.getBytes(), institutionId, campagneName,objectMapper.readValue(date, Date.class));
        return campagneService.toDTO(campagne);
    }

    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
    @PostMapping("/createTemplate")
    public CertificationDTO createCertificationTemplate(@RequestParam("universityLogo") MultipartFile universityLogo,
                                                        @RequestParam("universityStamp") MultipartFile universityStamp,
                                                        @RequestParam("certificationDTO") String certificationString,
                                                        @RequestParam("institutionId") String institutionId) throws IOException {
        Certification certification = institutionService.createInstitutionCertificateTemplate(institutionId,
                objectMapper.readValue(certificationString, Certification.class),
                universityLogo.getBytes(),
                universityStamp.getBytes());
        return certificationService.toDTO(certification);
    }

    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
    @PostMapping("/uploadContract")
    public InstitutionDTO uploadCertificateContract(@RequestBody UploadBlockChainContractDTO uploadBlockChainContractDTO) throws Exception {
        Institution institution =  institutionService.uploadCertificateContract(uploadBlockChainContractDTO.getId(),uploadBlockChainContractDTO.getWalletPasSword());

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
