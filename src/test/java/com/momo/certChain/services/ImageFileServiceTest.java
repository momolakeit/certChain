package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.repositories.ImageFileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageFileServiceTest {

    @InjectMocks
    private ImageFileService imageFileService;

    @Mock
    private ImageFileRepository imageFileRepository;

    @Test
    public void createImageFileTest() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new File("./src/test/resources/MOCK_DATA.xlsx"));
        when(imageFileRepository.save(any(ImageFile.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        ImageFile imageFile = imageFileService.createImageFile(bytes);
        assertEquals(bytes, imageFile.getBytes());
    }

    @Test
    public void findImageFile() {
        ImageFile imageFile = new ImageFile();
        when(imageFileRepository.findById(anyString())).thenReturn(Optional.of(imageFile));
        ImageFile returnImageFile = imageFileService.findImageFile("123456");
        assertEquals(imageFile, returnImageFile);

    }

    @Test
    public void findImageNotFoundThrowsException() {
        when(imageFileRepository.findById(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            imageFileService.findImageFile("123456");
        });

    }

    @Test
    public void saveImageFile() {
        ImageFile imageFile = new ImageFile();
        when(imageFileRepository.save(any(ImageFile.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        ImageFile returnImageFile = imageFileService.saveImageFile(imageFile);
        assertEquals(imageFile, returnImageFile);
    }

}