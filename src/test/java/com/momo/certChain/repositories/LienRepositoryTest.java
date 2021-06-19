package com.momo.certChain.repositories;

import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Lien;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LienRepositoryTest {

    @Autowired
    private LienRepository lienRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    Certification certification;

    @BeforeEach
    public void init() {
        certification = certificationRepository.save(new Certification());

        createLienWithCertification();

        createLienWithCertification();

    }

    @Test
    public void findAllByCertificationId(){
        List<Lien> lienList = lienRepository.findLienByCertificationId(certification.getId());

        assertEquals(2,lienList.size());
    }

    @Test
    public void findAllByCertificationIdCleanTableLienNoData(){
        lienRepository.deleteAll();

        List<Lien> lienList = lienRepository.findLienByCertificationId(certification.getId());

        assertEquals(0,lienList.size());
    }

    @Test
    public void findAllByCertificationNoLienForData(){
        List<Lien> lienList = lienRepository.findLienByCertificationId("123456");

        assertEquals(0,lienList.size());
    }

    private Lien createLienWithCertification() {
        Lien lien = new Lien();
        lien.setCertification(certification);
        return lienRepository.save(lien);
    }
}