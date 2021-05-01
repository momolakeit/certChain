package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.InstitutionMapper;
import com.momo.certChain.model.data.*;
import com.momo.certChain.model.dto.AddressDTO;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.repositories.InstitutionRepository;
import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.excel.ExcelService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    private final AddressService addressService;

    private final ContractService contractService;

    private final CertificationService certificationService;

    private final ExcelService excelService;

    private final HumanUserService userService;

    public InstitutionService(InstitutionRepository institutionRepository,
                              AddressService addressService,
                              ContractService contractService,
                              CertificationService certificationService,
                              ExcelService excelService,
                              HumanUserService userService) {
        this.institutionRepository = institutionRepository;
        this.addressService = addressService;
        this.contractService = contractService;
        this.certificationService = certificationService;
        this.excelService = excelService;
        this.userService = userService;
    }

    public Institution createInstitution(AddressDTO addressDTO, InstitutionDTO institutionDTO) {
        Address address = addressService.createAddress(addressDTO.getStreet(), addressDTO.getCity(), addressDTO.getProvince(), addressDTO.getPostalCode(), addressDTO.getCountry());
        Institution institution = InstitutionMapper.instance.toEntity(institutionDTO);
        institution.setAddress(address);
        return saveInstitution(institution);
    }

    public Institution uploadCertificateContract(String uuid,String privateKey) throws Exception {
        Institution institution = getInstitution(uuid);
        institution.setContractAddress(contractService.uploadContract(privateKey));
        return saveInstitution(institution);
    }

    public void uploadCertificationsToBlockChain(byte[] bytes, String uuid) throws Exception {
        Institution institution = getInstitution(uuid);
        List<HumanUser> studentList = excelService.readStudentsFromExcel(bytes);
        linkInstitutionAndStudents(institution, studentList);
        studentList = userService.saveMultipleUser(studentList);
        studentList.forEach(humanUser -> {
            Student student = (Student) humanUser;
            try {
                certificationService.uploadCertificationToBlockChain(student.getCertifications().get(0), institution.getCertificationTemplate(), "", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Institution saveInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }

    public Institution getInstitution(String uuid) {
        return institutionRepository.findById(uuid).orElseThrow(this::institutionNotFound);
    }

    private void linkInstitutionAndStudents(Institution institution, List<HumanUser> students) {
        for (HumanUser student : students) {
            student.setInstitution(institution);
        }
    }

    private ObjectNotFoundException institutionNotFound() {
        return new ObjectNotFoundException("Institution");
    }
}
