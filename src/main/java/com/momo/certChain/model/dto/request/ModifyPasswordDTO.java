package com.momo.certChain.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyPasswordDTO {
    private String uuid ;
    private String oldPassword;
    private String password;
    private String passwordConfirmation;
    private String certificateEncKey;
}
