package base.es.controllers;

import base.es.model.Contractor;
import base.es.services.ContractorService;
import base.es.utils.DataFileReader;
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
@RequestMapping("/contractor")
public class ContractorController {

    @Autowired
    ContractorService contractorService;

    private static RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            )
    );

    @GetMapping("/table")
    public String table() {
        return "contractors";
    }

    @GetMapping("/load")
    @ResponseStatus(OK)
    public void load() {
        List<Contractor> contractors1 = null;
        List<Contractor> contractors2 = null;
        try {
            contractors1 = DataFileReader.importContractors1("C:\\Users\\fyatskevich\\Desktop\\2019_05_14_MDM\\contractors_data\\contractors1.txt");
            contractors2 = DataFileReader.importContractors1("C:\\Users\\fyatskevich\\Desktop\\2019_05_14_MDM\\contractors_data\\contractors2.txt");


            for (Contractor contractor: contractors1) {
                contractorService.index(contractor);
            }

            for (Contractor contractor: contractors2) {
                contractorService.index(contractor);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/searchDuplicates")
    @ResponseBody
    public List<Contractor> search(@RequestBody List<Map<String, String>> criteria) {
        return contractorService.searchDuplicates(criteria);
    }

    @GetMapping("/searchDuplicatesByFieldAndPrefix")
    @ResponseBody
    public List<Contractor> searchDuplicatesByFieldAndPrefix(@RequestParam String query, @RequestParam String fieldname) {
        return contractorService.searchDuplicatesByFieldAndPrefix(fieldname, query);
    }

}
