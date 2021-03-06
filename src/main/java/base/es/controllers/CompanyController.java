package base.es.controllers;

import base.es.model.Company;
import base.es.utils.DataFileReader;
import base.es.services.CompanyService;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    private static RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            )
    );

    @GetMapping("/table")
    public String table() {
        return "companies";
    }

    @GetMapping("/load")
    @ResponseStatus(OK)
    public void load() {
        List<Company> companies = null;
        try {
            companies = DataFileReader.importCompanies("C:\\Users\\fyatskevich\\Desktop\\2019_05_14_MDM\\input_data.txt");

            for (Company company: companies) {
                companyService.index(company);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/search")
    @ResponseBody
    public List<Company> search(@RequestBody List<Map<String, String>> criteria) {
         return companyService.searchDuplicatedCompaniesByComplicatedCriteria1(criteria);
    }

    @GetMapping("/searchDuplicatesByFieldAndPrefix")
    @ResponseBody
    public List<Company> searchDuplicatesByFieldAndPrefix(@RequestParam String query, @RequestParam String fieldname) {
        return companyService.searchDuplicatesByFieldAndPrefix(fieldname, query);
    }

}
