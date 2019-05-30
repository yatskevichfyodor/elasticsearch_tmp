package base.es.utils;

import base.es.model.Company;
import base.es.model.Contractor;

import java.util.Map;

public class Map2EntityConverter {

    public static Company convertMap2Company(Map map) {
        Company company = new Company();
        company.setName((String) map.get("name"));
        company.setAddress((String) map.get("address"));
        company.setPhone((String) map.get("phone"));
        company.setEmail((String) map.get("email"));
        company.setSite((String) map.get("site"));
        company.setSection((String) map.get("section"));
        company.setIndustry((String) map.get("industry"));
        return company;
    }

    public static Contractor convertMap2Contractor(Map map) {
        Contractor contractor = new Contractor();
        contractor.setINN((String) map.get("INN"));
        contractor.setKPP((String) map.get("KPP"));
        contractor.setPhone((String) map.get("phone"));
        contractor.setCode1((String) map.get("code1"));
        contractor.setName((String) map.get("name"));
        contractor.setNameWorking((String) map.get("nameWorking"));
        contractor.setNameByDocuments((String) map.get("nameByDocuments"));
        contractor.setLegalAddress((String) map.get("legalAddress"));
        contractor.setActualAddress((String) map.get("actualAddress"));
        contractor.setMainBankAccount((String) map.get("mainBankAccount"));
        contractor.setCode2((String) map.get("code2"));
        contractor.setShortName((String) map.get("shortName"));
        contractor.setOwnershipType((String) map.get("ownershipType"));
        contractor.setFullName((String) map.get("fullName"));
        contractor.setAddress((String) map.get("address"));
        contractor.setHolding((String) map.get("holding"));

        return contractor;
    }

}
