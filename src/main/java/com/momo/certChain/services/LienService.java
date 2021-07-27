package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.mapping.LienMapper;
import com.momo.certChain.model.CreatedLien;
import com.momo.certChain.model.Type;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Lien;
import com.momo.certChain.model.dto.LienDTO;
import com.momo.certChain.repositories.LienRepository;
import com.momo.certChain.services.security.EncryptionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class LienService {

    private final LienRepository lienRepository;

    private final EncryptionService encryptionService;

    public LienService(LienRepository lienRepository, EncryptionService encryptionService) {
        this.lienRepository = lienRepository;
        this.encryptionService = encryptionService;
    }

    public CreatedLien createLien(String encKey, Date date, String titre, Certification certification) {
        isDateBeforeNow(date);

        String generatedPassword = RandomStringUtils.randomAlphanumeric(11);

        return getCreatedLien(encKey, date, titre, certification, generatedPassword,Type.UTILISATEUR_EXTERNE);
    }

    public CreatedLien createLienAccesPourProprietaireCertificat(String userPassword, String encKey, Certification certification) throws ParseException {
        return getCreatedLien(encKey, new SimpleDateFormat("dd/MM/yyyy").parse("31/12/9999"), "", certification, userPassword,Type.PROPRIETAIRE_CERTIFICAT);
    }

    public Lien getLien(String lienId, String password) {
        Lien lien = lienRepository.findById(lienId).orElseThrow(() -> new ObjectNotFoundException("Lien"));

        lien = LienMapper.instance.toSimple(lien);

        lien.setCertificateEncKey(encryptionService.decryptDataForCertificate(password, lien.getCertificateEncKey(), lien.getSalt()));

        return lien;
    }

    public List<Lien> findAllLienForCertificationUtilisateur_Externe(String certId) {
        return lienRepository.findLienByCertificationIdAndType(certId, Type.UTILISATEUR_EXTERNE);
    }

    public List<Lien> findAllLienForCertificationProprietaire_Certificat(String certId) {
        return lienRepository.findLienByCertificationIdAndType(certId, Type.PROPRIETAIRE_CERTIFICAT);
    }

    public LienDTO toDTO(Lien lien) {
        return LienMapper.instance.toDTO(lien);
    }

    private Lien createLien(String encKey, String generatedPassword, String salt, Date date, String titre, Certification certification, Type type) {
        Lien lien = new Lien();
        lien.setSalt(salt);
        lien.setCertificateEncKey(encryptionService.encryptData(generatedPassword, encKey, salt));
        lien.setTitre(titre);
        lien.setDateExpiration(date);
        lien.setCertification(certification);
        lien.setType(type);
        return lien;
    }

    private void isDateBeforeNow(Date date) {
        if (date.compareTo(new Date(System.currentTimeMillis())) < 1) {
            throw new ValidationException("La date d'expiration ne peux être dans le passé");
        }
    }

    private Lien saveLien(Lien lien) {
        return lienRepository.save(lien);
    }


    private CreatedLien getCreatedLien(String encKey, Date date, String titre, Certification certification, String generatedPassword,Type type) {
        String salt = encryptionService.generateSalt();

        Lien lien = saveLien(createLien(encKey, generatedPassword, salt, date, titre, certification,type));

        return new CreatedLien(lien, generatedPassword);
    }
}
