package com.momo.certChain.controller;

import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.User;
import com.momo.certChain.model.dto.HumanUserDTO;
import com.momo.certChain.model.dto.UserDTO;
import com.momo.certChain.model.dto.request.ModifyPasswordDTO;
import com.momo.certChain.services.HumanUserService;
import com.momo.certChain.services.UserService;
import org.dom4j.util.UserDataAttribute;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable String userId){
        User user = userService.getUser(userId);
        return userService.toDto(user);
    }
}
