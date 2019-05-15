package base.es.controllers;

import base.es.model.Company;
import base.es.DataFileReader;
import base.es.services.CompanyService;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @GetMapping("/loadCompanies")
    @ResponseStatus(OK)
    public void loadCompanies() {
        System.out.println("- loadCompanies method started");

        List<Company> companies = null;
        try {
            companies = DataFileReader.read("C:\\Users\\fyatskevich\\Desktop\\2019_05_14_MZK\\input_data.txt");
        } catch(Exception e) {
            e.printStackTrace();
        }

        for (Company company: companies) {
            companyService.indexConpany(company);
        }

        System.out.println("tmp");
    }

    @GetMapping("/searchCompanies")
    @ResponseBody
    public List<Company> searchCompanies(@RequestParam String query) {
        System.out.println("- searchCompanies method started");

        List<Company> companies = CompanyService.search(query);

        System.out.println("- searchCompanies method finished");

        return companies;
    }

}
