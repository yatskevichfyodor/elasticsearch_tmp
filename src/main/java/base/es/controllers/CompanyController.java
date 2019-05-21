package base.es.controllers;

import base.es.model.Company;
import base.es.utils.DataFileReader;
import base.es.services.CompanyService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class CompanyController {

    @Autowired
    CompanyService companyService;

    private static RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            )
    );

    @GetMapping("/loadCompanies")
    @ResponseStatus(OK)
    public void loadCompanies() {
        System.out.println("- loadCompanies method started");

        List<Company> companies = null;
        try {
            companies = DataFileReader.read("C:\\Users\\fyatskevich\\Desktop\\2019_05_14_MDM\\input_data.txt");
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

    /**
     * curl -X POST "localhost:9200/companies/_search?pretty" -H 'Content-Type: application/json' -d'
     * {
     *    "query":{
     *       "prefix": {
     *          "email": "dj.market"
     *       }
     *    },
     *     "aggs": {
     *         "email_duplicates": {
     *             "terms": {
     *                 "field": "email"
     *             },
     *             "aggs": {
     *                 "companies_duplicates": {
     *                     "top_hits": {
     *                         "sort": [
     *                             {
     *                                 "email": {
     *                                     "order": "asc"
     *                                 }
     *                             }
     *                         ],
     *                         "size": 100
     *                     }
     *                 }
     *             }
     *         }
     *     }
     * }
     * '
     */
    @GetMapping("/searchCompaniesWithDuplicatedEmails")
    @ResponseBody
    public List<Company> searchCompaniesWithDuplicatedEmails(@RequestParam String query) {
        System.out.println("- searchCompaniesWithDuplicatedEmails method started");

        List<Company> companies = companyService.searchDuplicatedCompaniesByFieldAndPrefix("email", query);

        System.out.println("- searchCompaniesWithDuplicatedEmails method finished");

        return companies;
    }

    @GetMapping("/searchCompaniesWithDuplicatedAddresses")
    @ResponseBody
    public List<Company> searchCompaniesWithDuplicatedAddresses(@RequestParam String query) {
        System.out.println("- searchCompaniesWithDuplicatedAddresses method started");

        List<Company> companies = companyService.searchDuplicatedCompaniesByFieldAndPrefix("address", query);

        System.out.println("- searchCompaniesWithDuplicatedAddresses method finished");

        return companies;
    }

    @GetMapping("/searchCompaniesWithDuplicatedFields")
    @ResponseBody
    public List<Company> searchCompaniesWithDuplicatedFields(@RequestParam String query, @RequestParam String fieldname) {
        System.out.println("- searchCompaniesWithDuplicatedFields method started");

        List<Company> companies = companyService.searchDuplicatedCompaniesByFieldAndPrefix(fieldname, query);

        System.out.println("- searchCompaniesWithDuplicatedFields method finished");

        return companies;
    }

    @PostMapping("/search")
    @ResponseBody
    public List<Company> search(@RequestBody List<Map<String, String>> criteria) {
        System.out.println("- search method started");

        List<Company> companies = companyService.searchDuplicatedCompaniesByComplicatedCriteria(criteria);

        System.out.println("- search method finished");

        return companies;
    }

}
