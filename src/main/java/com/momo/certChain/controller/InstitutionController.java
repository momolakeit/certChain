package com.momo.certChain.controller;

import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.dto.AddressDTO;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.model.dto.request.CreateInstitutionDTO;
import com.momo.certChain.services.InstitutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.crypto.CipherException;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@RestController
@RequestMapping("/institution")
public class InstitutionController extends BaseController {

    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @PostMapping
    public InstitutionDTO createInstitution(@RequestBody CreateInstitutionDTO createInstitutionDTO) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, CipherException {
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
    public InstitutionDTO getInstitution(@PathVariable String institutionId){
        return institutionService.toDTO(institutionService.getInstitution(institutionId));
    }

    @PostMapping("/uploadCertification/{institutionId}")
    public ResponseEntity uploadCertifications(@RequestParam("file") MultipartFile file,
                                               @RequestParam("walletPassword") String walletPassword,
                                               @PathVariable String institutionId,
                                               @RequestParam("campagneName") String campagneName ) throws Exception {
        institutionService.uploadCertificationsToBlockChain(file.getBytes(),institutionId,walletPassword,campagneName);
        return ResponseEntity.ok().build();
    }

}
