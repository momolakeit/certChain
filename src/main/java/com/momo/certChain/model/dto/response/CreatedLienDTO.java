package com.momo.certChain.model.dto.response;

import com.momo.certChain.model.data.Lien;
import com.momo.certChain.model.dto.LienDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreatedLienDTO {
    private LienDTO lien;

    private String generatedPassword;

}
