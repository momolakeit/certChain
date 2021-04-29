package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.InstitutionMapper;
import com.momo.certChain.model.data.Address;
import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.dto.AddressDTO;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.repositories.InstitutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    private final AddressService addressService;

    public InstitutionService(InstitutionRepository institutionRepository, AddressService addressService) {
        this.institutionRepository = institutionRepository;
        this.addressService = addressService;
    }

    public Institution createInstitution(AddressDTO addressDTO, InstitutionDTO institutionDTO) {
        Address address = addressService.createAddress(addressDTO.getStreet(), addressDTO.getCity(), addressDTO.getProvince(), addressDTO.getPostalCode(), addressDTO.getCountry());
        Institution institution = InstitutionMapper.instance.toEntity(institutionDTO);
        institution.setAddress(address);
        return institutionRepository.save(institution);
    }
    public Institution getInstitution(String uuid) {
        return institutionRepository.findById(uuid).orElseThrow(this::institutionNotFound);
    }

    private ObjectNotFoundException institutionNotFound(){
        return new ObjectNotFoundException("Institution");
    }
}
