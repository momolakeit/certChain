package com.momo.certChain.services.excel;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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
        bytes = TestUtils.getExcelByteArray();
    }

    @Test
    public void convertStudentFromExcel() throws IOException {
        List<HumanUser> students = excelService.readStudentsFromExcel(bytes);
        assertEquals(500,students.size());
        students.forEach(student -> {
            Student stu = (Student) student;
            Certification certification = stu.getCertifications().stream().findFirst().orElse(null);
            assertNotNull(stu.getAddress().getCity());
            assertNotNull(stu.getAddress().getPostalCode());
            assertNotNull(stu.getAddress().getCountry());
            assertNotNull(stu.getAddress().getStreet());
            assertNotNull(stu.getAddress().getProvince());
            assertNotNull(stu.getPrenom());
            assertNotNull(stu.getNom());
            assertNotNull(certification.getProgram());
        });

    }

}