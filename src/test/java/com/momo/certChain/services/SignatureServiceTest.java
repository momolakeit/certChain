package com.momo.certChain.services;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.repositories.SignatureRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignatureServiceTest {
    @InjectMocks
    private SignatureService signatureService;

    @Mock
    private ImageFileService imageFileService;

    @Mock
    private SignatureRepository signatureRepository;

    @Test
    public void createSignature() {
        String authorName = "John Doe";

        when(signatureRepository.save(any(Signature.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Signature signature = signatureService.createSignature(authorName);

        assertNotNull(signature);
        assertEquals(authorName, signature.getAuthorName());
    }

    @Test
    public void addSignatureImage() throws IOException {
        ImageFile imageFile = TestUtils.createImageFile();

        when(signatureRepository.save(any(Signature.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(signatureRepository.findById(anyString())).thenReturn(Optional.of(new Signature()));
        when(imageFileService.createImageFile(any(byte[].class))).thenReturn(imageFile);

        Signature signature = signatureService.addSignatureImage("123456", imageFile.getBytes());

        assertEquals(imageFile, signature.getSignatureImage());
    }

    @Test
    public void getSignatureTest() throws IOException {
        Signature signature = TestUtils.createSignature();

        when(signatureRepository.findById(anyString())).thenReturn(Optional.of(signature));

        Signature returnValueSignature = signatureService.getSignature("123456");

        assertEquals(signature.getAuthorName(), returnValueSignature.getAuthorName());
        assertEquals(signature.getSignatureImage(), returnValueSignature.getSignatureImage());
    }

    @Test
    public void getSignatureNotFoundTest() {
        when(signatureRepository.findById(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class,()->{
            signatureService.getSignature("123456");
        });
    }

    @Test
    public void saveSignature() throws IOException {
        Signature signature = TestUtils.createSignature();
        when(signatureRepository.save(any(Signature.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Signature returnValueSignature = signatureService.saveSignature(signature);
        assertEquals(signature.getAuthorName(), returnValueSignature.getAuthorName());
    }
}