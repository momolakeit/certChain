package com.momo.certChain.controller;

import com.momo.certChain.services.HumanUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/humanUser")
public class HumanUserController {

    private final HumanUserService humanUserService;

    public HumanUserController(HumanUserService humanUserService) {
        this.humanUserService = humanUserService;
    }

}
