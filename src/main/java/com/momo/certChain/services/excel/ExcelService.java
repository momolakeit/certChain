package com.momo.certChain.services.excel;

import com.momo.certChain.model.data.Address;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.utils.ListUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/***Ordre du data : prenom nom username street city province postalCode country program ***/
@Service
public class ExcelService {

    public List<Student> readStudentsFromExcel(byte[] bytes) throws IOException {
        List<Student> studentList = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes));
        Sheet sheet = workbook.getSheetAt(0);
        int rowCounter = 0;
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (rowCounter > 0) {
                studentList.add(studentFromRow(row));
            }
            rowCounter++;
        }
        return studentList;
    }

    private Student studentFromRow(Row row) {
        Student student = new Student();
        Address address = new Address();
        Certification certification = new Certification();
        student.setPrenom(cellToString(row.getCell(0)));
        student.setNom(cellToString(row.getCell(1)));
        student.setUsername(cellToString(row.getCell(2)));
        address.setStreet(cellToString(row.getCell(3)));
        address.setCity(cellToString(row.getCell(4)));
        address.setProvince(cellToString(row.getCell(5)));
        address.setPostalCode(cellToString(row.getCell(6)));
        address.setCountry(cellToString(row.getCell(7)));
        certification.setProgram(cellToString(row.getCell(8)));
        student.setAddress(address);
        student.setCertifications(ListUtils.ajouterObjectAListe(certification,student.getCertifications()));
        return student;
    }

    private String cellToString(Cell cell){
        String value = "";
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue();
                 break;
            case NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                break;
        }
        return value;
    }
}
