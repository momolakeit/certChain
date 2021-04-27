package com.momo.certChain.services.excel;

import com.momo.certChain.TestUtils;
import com.momo.certChain.model.data.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExcelServiceTest {
    @InjectMocks
    private ExcelService excelService;


    byte [] bytes;

    @BeforeEach
    public void setUp() throws IOException {
        bytes = FileUtils.readFileToByteArray(new File("./src/test/resources/MOCK_DATA.xlsx"));
    }

    @Test
    public void convertStudentFromExcel() throws IOException {
        List<Student> students = excelService.readStudentsFromExcel(bytes);
        assertEquals(500,students.size());
        students.forEach(student -> {
            assertNotNull(student.getAddress().getCity());
            assertNotNull(student.getAddress().getPostalCode());
            assertNotNull(student.getAddress().getCountry());
            assertNotNull(student.getAddress().getStreet());
            assertNotNull(student.getAddress().getProvince());
            assertNotNull(student.getPrenom());
            assertNotNull(student.getNom());
        });

    }

}