package com.momo.certChain.services;

import com.momo.certChain.model.data.Institution;
import com.momo.certChain.repositories.InstitutionRepository;
import org.springframework.stereotype.Service;

@Service
public class InstitutionService {

    private InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public Institution createInstitution(){
        return null;
    }
}
