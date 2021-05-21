package com.momo.certChain.repositories;

import com.momo.certChain.TestUtils;
import com.momo.certChain.model.data.Institution;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class InstitutionRepositoryTest {
    @Autowired
    private InstitutionRepository institutionRepository;

    @Test
    public void findAllNonApprouvedInstitution() {
        institutionRepository.save(getInstitution());
        institutionRepository.save(getInstitutionNonApprouved());
        institutionRepository.save(getInstitutionNonApprouved());

        List<Institution> institutionList = institutionRepository.findNonApprouvedInstitution();

        assertEquals(2, institutionList.size());
    }

    @Test
    public void findAllNonApprouvedInstitutionNoNonApprouvedInstitution() {
        institutionRepository.save(getInstitution());
        institutionRepository.save(getInstitution());
        institutionRepository.save(getInstitution());

        List<Institution> institutionList = institutionRepository.findNonApprouvedInstitution();

        assertEquals(0, institutionList.size());
    }

    private Institution getInstitutionNonApprouved() {
        Institution institution = getInstitution();
        institution.setApprouved(false);
        return institution;
    }

    private Institution getInstitution() {
        Institution institution = TestUtils.createInstitution();
        institution.setAddress(null);
        return institution;
    }
}
