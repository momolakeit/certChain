package com.momo.certChain.controller;

import com.momo.certChain.model.data.Campagne;
import com.momo.certChain.model.dto.CampagneDTO;
import com.momo.certChain.model.dto.request.RunCampagneDTO;
import com.momo.certChain.services.CampagneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campagne")
public class CampagneController extends BaseController {
    private final CampagneService campagneService;

    public CampagneController(CampagneService campagneService) {
        this.campagneService = campagneService;
    }
                
    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
    @GetMapping("/{campagneId}")
    public CampagneDTO fetchCampagne(@PathVariable String campagneId){
        Campagne campagne = campagneService.getCampagne(campagneId);
        return campagneService.toDTO(campagne);
    }

    @PreAuthorize("hasAuthority('ROLE_INSTITUTION')")
    @PutMapping("/runCampagne")
    public ResponseEntity runCampagne(@RequestBody RunCampagneDTO runCampagneDTO) throws Exception {
         campagneService.runCampagne(runCampagneDTO.getCampagneId(),runCampagneDTO.getWalletPassword());
         return ResponseEntity.ok().build();
    }
}
