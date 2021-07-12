package com.momo.certChain.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropriaitaireLienDTO {

    private String certificationId;

    private String certificationPassword;

    private String certEncKey;

}
