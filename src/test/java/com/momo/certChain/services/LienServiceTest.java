package com.momo.certChain.services;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.model.data.Lien;
import com.momo.certChain.repositories.LienRepository;
import com.momo.certChain.services.security.EncryptionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LienServiceTest {

    @InjectMocks
    private LienService lienService;

    @Captor
    private ArgumentCaptor<Lien> lienArgumentCaptor;

    @Mock
    private LienRepository lienRepository;

    @Mock
    private EncryptionService encryptionService;

    MockedStatic<RandomStringUtils> randomStringUtilsMockedStatic;

    private final Long anneeEnMilliseconde = 31536000000L;

    private final String encKeyToEncrypt = "encryptMe";

    @BeforeEach
    public void init() {
        randomStringUtilsMockedStatic = mockStatic(RandomStringUtils.class);
    }

    @AfterEach
    public void destroy() {
        randomStringUtilsMockedStatic.closeOnDemand();
    }

    @Test
    public void createLienTest() {
        String newEncKey = "super secret";
        String salt = "salyString";
        Date dateFin = new Date(System.currentTimeMillis() + anneeEnMilliseconde);

        when(encryptionService.generateSalt()).thenReturn(salt);
        when(encryptionService.encryptData(anyString(), anyString(), anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(1));
        when(lienRepository.save(any(Lien.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        randomStringUtilsMockedStatic.when(() -> RandomStringUtils.randomAlphanumeric(11)).thenReturn(newEncKey);

        String generatedEncKey = lienService.createLien(encKeyToEncrypt, dateFin);

        verify(lienRepository).save(lienArgumentCaptor.capture());

        Lien lien = lienArgumentCaptor.getValue();

        assertEquals(salt, lien.getSalt());
        assertEquals(encKeyToEncrypt, lien.getCertificateEncKey());
        assertEquals(dateFin, lien.getDateExpiration());
        assertEquals(newEncKey, generatedEncKey);
    }

    @Test
    public void createLienDateExpBeforeThrowExceptionTest() {
        Date dateFin = new Date(System.currentTimeMillis() - anneeEnMilliseconde);

        Assertions.assertThrows(ValidationException.class, () -> {
            lienService.createLien(encKeyToEncrypt, dateFin);
        });

    }

    @Test
    public void testGetLien() {
        when(lienRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createLien()));
        when(encryptionService.decryptData(anyString(), anyString(), anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(1));

        TestUtils.assertLien(lienService.getLien("123456", "password"));

    }

    @Test
    public void testGetNotFoundLien() {
        when(lienRepository.findById(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class,()->{
            lienService.getLien("123456", "password");
        });

    }
}