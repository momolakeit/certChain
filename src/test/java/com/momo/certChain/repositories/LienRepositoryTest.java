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

        createLienTypeUtilisateurExterneWithCertification(certification);

        createLienTypeUtilisateurExterneWithCertification(certification);

        createLienTypeUtilisateurExterneWithCertification(certificationRepository.save(new Certification()));

    }

    @Test
    public void findAllByCertificationId(){
        List<Lien> lienList = lienRepository.findLienByCertificationIdAndType(certification.getId(),Lien.Type.UTILISATEUR_EXTERNE);

        assertEquals(2,lienList.size());
    }


    @Test
    public void findAllByCertificationIdAndTypePropriataireExterne(){
        createLienTypePropriataireWithCertification(certification);

        List<Lien> lienList = lienRepository.findLienByCertificationIdAndType(certification.getId(),Lien.Type.PROPRIAITAIRE_CERTIFICAT);

        assertEquals(1,lienList.size());
    }

    @Test
    public void findAllByCertificationIdCleanTableLienNoData(){
        lienRepository.deleteAll();

        List<Lien> lienList = lienRepository.findLienByCertificationIdAndType(certification.getId(),Lien.Type.UTILISATEUR_EXTERNE);

        assertEquals(0,lienList.size());
    }

    @Test
    public void findAllByCertificationNoLienForData(){
        List<Lien> lienList = lienRepository.findLienByCertificationIdAndType("123456",Lien.Type.UTILISATEUR_EXTERNE);

        assertEquals(0,lienList.size());
    }

    private Lien createLienWithCertification(Certification certification,Lien.Type type) {
        Lien lien = new Lien();
        lien.setCertification(certification);
        lien.setType(type);
        return lien;
    }

    private void createLienTypeUtilisateurExterneWithCertification(Certification certification) {
        lienRepository.save(createLienWithCertification(certification,Lien.Type.UTILISATEUR_EXTERNE));
    }

    private void createLienTypePropriataireWithCertification(Certification certification) {
        lienRepository.save(createLienWithCertification(certification,Lien.Type.PROPRIAITAIRE_CERTIFICAT));
    }
}