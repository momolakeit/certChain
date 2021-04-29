package com.momo.certChain;

import com.momo.certChain.model.data.*;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {
    private static final String street = "7894 boul maisonneuve";

    private static final String city = "Montreal";

    private static final String province = "Quebec";

    private static final String postalCode = "H1X2A2";

    private static final String country = "Canada";

    private static final String username = "username";

    private static final String password = "password";

    private static final String institutionName = "MomoU";

    private static final String id = "123456";

    private static final String program = "genie informatique";

    private static final String authorName="John Doe";

    public static void assertAddress(Address returnVal){
        Address address = createAddress();
        assertEquals(address.getCity(),returnVal.getCity());
        assertEquals(address.getPostalCode(),returnVal.getPostalCode());
        assertEquals(address.getCountry(),returnVal.getCountry());
        assertEquals(address.getStreet(),returnVal.getStreet());
        assertEquals(address.getProvince(),returnVal.getProvince());
    }
    public static void assertBaseUser(User returnVal){
        User user = initBasicUser(new User());
        assertEquals(user.getPassword(),returnVal.getPassword());
        assertEquals(user.getUsername(),returnVal.getUsername());
    }

    public static void assertInstitution(Institution returnVal) {
        assertBaseUser(returnVal);
        assertEquals(institutionName,returnVal.getName());
    }
    public static void assertCertification(Certification returnVal) {
        Certification certification = createCertification();
        assertEquals(certification.getId(),returnVal.getId());
        assertEquals(certification.getProgram(),returnVal.getProgram());
    }

    public static Address createAddress() {
        Address address = new Address();
        address.setCountry(country);
        address.setPostalCode(postalCode);
        address.setProvince(province);
        address.setCity(city);
        address.setStreet(street);
        return address;
    }

    public static Student createStudent(){
        Student student = (Student) initBasicUser(new Student());
        student.setInstitution(createInstitution());
        return student;
    }
    public static Employee createEmploye(){
        Employee employee = (Employee) initBasicUser(new Employee());
        employee.setInstitution(createInstitution());
        return employee;
    }

    public static Institution createInstitution() {
        Institution institution = (Institution) initBasicUser(new Institution());
        institution.setName(institutionName);
        return institution;
    }
    public static Certification createCertification(){
        Certification certification = new Certification();
        certification.setId(id);
        certification.setProgram(program);
        certification.setDateOfIssuing(new Date(System.currentTimeMillis()));
        certification.setInstitution(createInstitution());
        certification.setStudent(createStudent());
        return certification;
    }
    public static ImageFile createImageFile() throws IOException {
        ImageFile imageFile = new ImageFile();
        imageFile.setBytes(getExcelByteArray());
        return imageFile;
    }
    public static Signature createSignature() throws IOException {
        Signature signature = new Signature();
        signature.setAuthorName(authorName);
        signature.setSignatureImage(createImageFile());
        return signature;
    }

    public static byte [] getExcelByteArray() throws IOException {
        return FileUtils.readFileToByteArray(new File("./src/test/resources/MOCK_DATA.xlsx"));
    }

    private static User initBasicUser(User user) {
        user.setAddress(createAddress());
        user.setUsername(username);
        user.setPassword(password);
        user.setId(id);
        return user;
    }

}
