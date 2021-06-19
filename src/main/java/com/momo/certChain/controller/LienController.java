package com.momo.certChain.controller;

import com.momo.certChain.model.data.User;
import com.momo.certChain.model.dto.LienDTO;
import com.momo.certChain.model.dto.UserDTO;
import com.momo.certChain.services.LienService;
import com.momo.certChain.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lien")
public class LienController extends BaseController {

    private final LienService lienService;

    public LienController(LienService lienService) {
        this.lienService = lienService;
    }

    @GetMapping("/findAllByCertification/{certificationId}")
    public List<LienDTO> getUser(@PathVariable String certificationId) {
        return lienService.findAllLienForCertification(certificationId)
                .stream()
                .map(lienService::toDTO)
                .collect(Collectors.toList());
    }
}
