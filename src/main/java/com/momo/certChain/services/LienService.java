package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.mapping.LienMapper;
import com.momo.certChain.model.CreatedLien;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Lien;
import com.momo.certChain.model.dto.LienDTO;
import com.momo.certChain.repositories.LienRepository;
import com.momo.certChain.services.security.EncryptionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Date;
import java.util.List;

@Service
public class LienService {

    private final LienRepository lienRepository;

    private final EncryptionService encryptionService;

    public LienService(LienRepository lienRepository, EncryptionService encryptionService) {
        this.lienRepository = lienRepository;
        this.encryptionService = encryptionService;
    }

    public CreatedLien createLien(String encKey, Date date, String titre, Certification certification){
        isDateBeforeNow(date);

        String generatedPassword = RandomStringUtils.randomAlphanumeric(11);

        String salt = encryptionService.generateSalt();

        Lien lien = saveLien(createLien(encKey, generatedPassword, salt,date,titre,certification));

        return new CreatedLien(lien,generatedPassword);
    }

    public Lien getLien(String lienId,String password){
        Lien lien = lienRepository.findById(lienId).orElseThrow(()->new ObjectNotFoundException("Lien"));

        lien = LienMapper.instance.toSimple(lien);

        lien.setCertificateEncKey(encryptionService.decryptData(password,lien.getCertificateEncKey(),lien.getSalt()));

        return lien;
    }

    public List<Lien> findAllLienForCertification(String certId){
        return lienRepository.findLienByCertificationId(certId);
    }

    public LienDTO toDTO(Lien lien){
        return LienMapper.instance.toDTO(lien);
    }

    private Lien createLien(String encKey, String generatedPassword, String salt,Date date,String titre,Certification certification) {
        Lien lien = new Lien();
        lien.setSalt(salt);
        lien.setCertificateEncKey(encryptionService.encryptData(generatedPassword, encKey, salt));
        lien.setTitre(titre);
        lien.setDateExpiration(date);
        lien.setCertification(certification);

        return lien;
    }

    private void isDateBeforeNow(Date date){
        if(date.compareTo(new Date(System.currentTimeMillis()))<1){
            throw new ValidationException("La date d'expiration ne peux être dans le passé");
        }
    }

    private Lien saveLien(Lien lien){
        return lienRepository.save(lien);
    }

}
