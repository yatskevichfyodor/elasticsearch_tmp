package base.es.utils;

import base.es.model.Company;

import java.util.Map;

public class Map2EntityConverter {

    public static Company convertMap2Entity(Map map) {
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

}
