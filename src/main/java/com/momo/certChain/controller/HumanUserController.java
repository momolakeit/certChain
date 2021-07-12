package com.momo.certChain.controller;

import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.dto.HumanUserDTO;
import com.momo.certChain.model.dto.request.ModifyPasswordDTO;
import com.momo.certChain.services.HumanUserService;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/humanUser")
public class HumanUserController extends BaseController {

    private final HumanUserService humanUserService;

    public HumanUserController(HumanUserService humanUserService) {
        this.humanUserService = humanUserService;
    }

    @PostMapping("/modifyPassword")
    public HumanUserDTO modifyPassword(@RequestBody ModifyPasswordDTO modifyPasswordDTO){
        HumanUser humanUser = humanUserService.modifyPassword(modifyPasswordDTO.getUuid(),
                modifyPasswordDTO.getOldPassword(),
                modifyPasswordDTO.getPassword(),
                modifyPasswordDTO.getPasswordConfirmation());
        return humanUserService.toDTO(humanUser);
    }
}
