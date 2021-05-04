package com.momo.certChain.model.dto.request;

import com.momo.certChain.model.dto.AddressDTO;
import com.momo.certChain.model.dto.InstitutionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateInstitutionDTO {

    private AddressDTO addressDTO;

    private InstitutionDTO institutionDTO;

    private String walletPassord;

}
