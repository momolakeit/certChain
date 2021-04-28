package com.momo.certChain.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SignatureDTO {

    private String id;

    private ImageFileDTO signatureImage;

    private String authorName;
}
