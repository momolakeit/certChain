package com.momo.certChain.services;

import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.model.data.Lien;
import com.momo.certChain.repositories.LienRepository;
import com.momo.certChain.services.security.EncryptionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LienService {

    private final LienRepository lienRepository;

    private final EncryptionService encryptionService;

    public LienService(LienRepository lienRepository, EncryptionService encryptionService) {
        this.lienRepository = lienRepository;
        this.encryptionService = encryptionService;
    }

    public String createLien(String encKey, Date date){
        isDateBeforeNow(date);

        String generatedPassword = RandomStringUtils.randomAlphanumeric(11);

        String salt = encryptionService.generateSalt();

        saveLien(createLien(encKey, generatedPassword, salt,date));

        return generatedPassword;
    }

    private Lien createLien(String encKey, String generatedPassword, String salt,Date date) {
        Lien lien = new Lien();
        lien.setSalt(salt);
        lien.setCertificateEncKey(encryptionService.encryptData(generatedPassword, encKey, salt));
        lien.setDateExpiration(date);

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
