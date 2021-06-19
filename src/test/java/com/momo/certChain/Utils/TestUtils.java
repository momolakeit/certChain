package com.momo.certChain.Utils;

import com.momo.certChain.model.data.*;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import org.web3j.crypto.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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

    private static final String certificateText="given to the best student in the world !";

    private static final String salt = "salt";

    private static final String contractAddress="contract address";

    private static final String campagneName="genie logiciel hiver";

    private static final String encKey="encKey";

    private static final Long premierJanvier2020 = 1577854800000L;

    private static final String titre = "Entrevue IBM";


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
        assertNotNull(certification.getDateOfIssuing());
    }

    public static void assertCertificationInstitution(Certification returnVal) throws IOException {
        assertNotNull(returnVal.getUniversityStamp());
        assertNotNull(returnVal.getUniversityStamp());
        assertEquals(2,returnVal.getSignatures().size());
    }


    public static void assertSignature(Signature signature) throws IOException {
        Signature returnSignature = createSignature();
        assertEquals(returnSignature.getId(),signature.getId());
        assertEquals(returnSignature.getAuthorName(),signature.getAuthorName());
    }

    public static void assertCampagne(Campagne campagne) throws IOException {
        Campagne returnCampagne = createCampagne();
        assertEquals(returnCampagne.getDate().getDay(),campagne.getDate().getDay());
        assertEquals(returnCampagne.getDate().getMonth(),campagne.getDate().getMonth());
        assertEquals(returnCampagne.getName(),campagne.getName());
        assertEquals(returnCampagne.getId(),campagne.getId());
    }

    public static void assertLien(Lien lien) {
        Lien returnLien = createLien();
        assertEquals(returnLien.getDateExpiration(),lien.getDateExpiration());
        assertEquals(returnLien.getCertificateEncKey(),lien.getCertificateEncKey());
        assertEquals(returnLien.getSalt(),lien.getSalt());
        assertEquals(returnLien.getTitre(),lien.getTitre());
        assertEquals(returnLien.getId(),lien.getId());
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
        institution.setApprouved(true);
        institution.setContractAddress(contractAddress);
        return institution;
    }
    public static Institution createInstitutionWithWallet() throws NoSuchAlgorithmException, CipherException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Institution institution = createInstitution();
        institution.setInstitutionWallet(createInstitutionWallet());
        return institution;
    }
    public static Certification createCertification(){
        Certification certification = new Certification();
        certification.setId(id);
        certification.setProgram(program);
        certification.setCertificateText(certificateText);
        certification.setDateOfIssuing(new Date(System.currentTimeMillis()));
        certification.setInstitution(createInstitution());
        certification.setStudent(createStudent());
        certification.setSalt(salt);
        return certification;
    }

    public static Certification createCertificationWithNoStudent(){
        Certification certification =TestUtils.createCertification();
        certification.setStudent(null);
        return certification;
    }
    public static Certification createCertificationTemplate() throws IOException {
        Certification certificationTemplate = createCertification();
        certificationTemplate.setSignatures(Arrays.asList(createSignature(), createSignature()));
        certificationTemplate.setUniversityStamp(TestUtils.createImageFile());
        certificationTemplate.setUniversityLogo(TestUtils.createImageFile());
        return certificationTemplate;
    }

    public static ImageFile createImageFile() throws IOException {
        ImageFile imageFile = new ImageFile();
        imageFile.setId(id);
        imageFile.setBytes(getExcelByteArray());
        return imageFile;
    }
    public static Signature createSignature() throws IOException {
        Signature signature = new Signature();
        signature.setId(id);
        signature.setAuthorName(authorName);
        signature.setSignatureImage(createImageFile());
        return signature;
    }

    public static byte [] getExcelByteArray() throws IOException {
        return FileUtils.readFileToByteArray(new File("./src/test/resources/MOCK_DATA.xlsx"));
    }

    public static InstitutionWallet createInstitutionWallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        WalletFile walletFile = Wallet.createStandard(password, ecKeyPair);
        InstitutionWallet institutionWallet = new InstitutionWallet();
        institutionWallet.setPublicAddress(walletFile.getAddress());
        institutionWallet.setPublicKey(ecKeyPair.getPublicKey().toString());
        institutionWallet.setPrivateKey(ecKeyPair.getPrivateKey().toString());
        institutionWallet.setSalt(salt);
        return institutionWallet;
    }

    public static Campagne createCampagne(){
        Campagne campagne = new Campagne();
        campagne.setId(id);
        campagne.setName(campagneName);
        campagne.setDate(new Date(System.currentTimeMillis()));
        return campagne;
    }

    public static Lien createLien(){
        Lien lien = new Lien();

        lien.setId(id);
        lien.setCertificateEncKey(encKey);
        lien.setDateExpiration(new Date(premierJanvier2020));
        lien.setSalt(salt);
        lien.setTitre(titre);

        return lien;
    }

    public static ECKeyPair createKeyPair(String privateKey, String publicKey){
        return new ECKeyPair(new BigInteger(privateKey),new BigInteger(publicKey));
    }

    private static User initBasicUser(User user) {
        user.setAddress(createAddress());
        user.setUsername(username);
        user.setPassword(password);
        user.setId(id);
        return user;
    }

}
