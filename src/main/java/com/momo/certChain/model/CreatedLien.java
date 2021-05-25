package com.momo.certChain.model;

import com.momo.certChain.model.data.Lien;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreatedLien {
    private Lien lien;

    private String generatedPassword;

}
