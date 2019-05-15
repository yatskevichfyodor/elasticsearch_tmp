package base.es.utils;

import base.es.model.Company;

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

    public static List<Company> read(String filePath) throws IOException {

        File file = new File(filePath);

        BufferedReader br = new BufferedReader(new FileReader(file));

        ArrayList<Company> result = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
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

        return result;


//        return Files.lines(Paths.get(filePath)).map((String line) -> {
//            String[] fields = line.split("\\|");
//            Company element = new Company();
//            element.setName(fields[0]);
//            element.setAddress(fields[1]);
//            element.setPhone(fields[2]);
//            element.setEmail(fields[3]);
//            element.setSite(fields[4]);
//            element.setSection(fields[5]);
//            element.setIndustry(fields[6]);
//            return element;
//        }).collect(Collectors.toCollection(ArrayList::new));
//
//        ArrayList<Company> result = new ArrayList<>();
//        Path path = Paths.get(filePath);
//        Files.lines(path).forEach( (String line) -> {
//            System.out.println("0");
//        });
//        Files.lines(path).forEach(System.out::println);
//        Files.lines(path).forEach((String line) -> {
//            String[] fields = line.split("\\|");
//            Company element = new Company();
//            element.setName(fields[0]);
//            element.setAddress(fields[1]);
//            element.setPhone(fields[2]);
//            element.setEmail(fields[3]);
//            element.setSite(fields[4]);
//            element.setSection(fields[5]);
//            element.setIndustry(fields[6]);
//            result.add(element);
//        });

//        return result;
    }
}
