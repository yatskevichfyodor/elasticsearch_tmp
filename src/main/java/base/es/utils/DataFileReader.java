package base.es.utils;

import base.es.model.Company;
import base.es.model.Contractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File should contain strings with data separated by "|" symbols
 * String in file are separated by "\n" symbol
 */
public class DataFileReader {

    public static List<Company> importCompanies(String filePath) throws IOException {

        File file = new File(filePath);

        BufferedReader br = new BufferedReader(new FileReader(file));

        ArrayList<Company> result = new ArrayList<>();

        String tempLineValue = null;
        int tempLineNumber = 0;
        try {
            String line;
            while ((line = br.readLine()) != null) {
                tempLineNumber++;
                tempLineValue = line;

                line += "|qwerty";
                String[] fields = line.split("\\|");

                Company element = new Company();
                element.setName(fields[0]);
                element.setAddress(fields[1]);
                element.setPhone(fields[2]);
                element.setEmail(fields[3]);
                element.setSite(fields[4]);
                element.setSection(fields[5]);
                element.setIndustry(fields[6]);
                result.add(element);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Line parse exception: [" + tempLineNumber + "]: [" + tempLineValue + "]", e);
        }

        return result;
    }

    public static List<Contractor> importContractors1(String filePath) throws IOException {

        File file = new File(filePath);

        BufferedReader br = new BufferedReader(new FileReader(file));

        ArrayList<Contractor> result = new ArrayList<>();

        String tempLineValue = null;
        int tempLineNumber = 0;
        try {
            String line;
            while ((line = br.readLine()) != null) {
                tempLineNumber++;
                tempLineValue = line;

                line += "|qwerty";
                String[] fields = line.split("\\|");

                Contractor element = new Contractor();
                element.setCode1(fields[0]);
                element.setName(fields[1]);
                element.setNameWorking(fields[2]);
                element.setNameByDocuments(fields[3]);
                element.setINN(fields[4]);
                element.setKPP(fields[5]);
                element.setLegalAddress(fields[6]);
                element.setActualAddress(fields[7]);
                element.setMainBankAccount(fields[8]);
                element.setPhone(fields[9]);
                result.add(element);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Line parse exception: [" + tempLineNumber + "]: [" + tempLineValue + "]", e);
        }

        return result;
    }

    public static List<Contractor> importContractors2(String filePath) throws IOException {

        File file = new File(filePath);

        BufferedReader br = new BufferedReader(new FileReader(file));

        ArrayList<Contractor> result = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] fields = line.split("\\|");
            Contractor element = new Contractor();
            element.setCode2(fields[0]);
            element.setShortName(fields[1]);
            element.setOwnershipType(fields[2]);
            element.setFullName(fields[3]);
            element.setINN(fields[4]);
            element.setKPP(fields[5]);
            element.setAddress(fields[6]);
            element.setPhone(fields[7]);
            element.setHolding(fields[8]);
            result.add(element);
        }

        return result;
    }

}
