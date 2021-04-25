package com.momo.certChain.services;

import com.momo.certChain.TestUtils;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.AddressMapper;
import com.momo.certChain.mapping.InstitutionMapper;
import com.momo.certChain.model.data.Address;
import com.momo.certChain.model.data.Institution;
import com.momo.certChain.repositories.InstitutionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {
    @InjectMocks
    private InstitutionService institutionService;

    @Mock
    private AddressService addressService;

    @Mock
    private InstitutionRepository institutionRepository;

    @Test
    public void createInstitutionTest() {
        Address address = TestUtils.createAddress();
        Institution institution = TestUtils.createInstitution();
        Mockito.when(addressService.createAddress(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(TestUtils.createAddress());
        Mockito.when(institutionRepository.save(any(Institution.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Institution returnVal = institutionService.createInstitution(AddressMapper.instance.toDTO(address), InstitutionMapper.instance.toDTO(institution));
        TestUtils.assertAddress(returnVal.getAddress());
        assertInstitution(institution, returnVal);

    }

    @Test
    public void fetchInstitution() {
        Institution institution = TestUtils.createInstitution();
        Mockito.when(institutionRepository.findById(anyString())).thenReturn(Optional.of(institution));
        Institution returnVal = institutionService.getInstitution("dsa48");
        assertInstitution(institution, returnVal);
    }

    @Test
    public void fetchInstitutionNotFoundLanceException() {
        Institution institution = TestUtils.createInstitution();
        Mockito.when(institutionRepository.findById(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            institutionService.getInstitution("dsa48");
        });
    }

    private void assertInstitution(Institution institution, Institution returnVal) {
        TestUtils.assertBaseUser(institution);
        assertEquals(institution.getName(), returnVal.getName());
    }
}