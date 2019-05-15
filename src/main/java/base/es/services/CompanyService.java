package base.es.services;

import base.es.model.Company;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanyService {

    private static int id = 0;

    private static RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            )
    );

    public void indexConpany(Company company) {
        Map<String, Object> jsonMap = new HashMap<>();
//        jsonMap.put("user", "galantis");
//        jsonMap.put("postDate", new Date());

        jsonMap.put("name", company.getName());
        jsonMap.put("address", company.getAddress());
        jsonMap.put("phone", company.getPhone());
        jsonMap.put("email", company.getEmail());
        jsonMap.put("site", company.getSite());
        jsonMap.put("section", company.getSection());
        jsonMap.put("industry", company.getIndustry());

        IndexRequest request = new IndexRequest("companies")
                .id(String.valueOf(id))
                .source(jsonMap)
                .opType("create");

        IndexResponse response;

        try {
            response = client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        id++;
    }

    public static List<Company> search(String query) {
        List<Company> results = new ArrayList<>();
        try {
            SearchRequest request = new SearchRequest("companies");

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            sourceBuilder.query(new MatchQueryBuilder("name", query));

            request.source(sourceBuilder);

            SearchResponse searchResponse = null;
            searchResponse = client.search(request, RequestOptions.DEFAULT);

            SearchHit[] searchHits = searchResponse.getHits().getHits();

            for (SearchHit searchHit: searchHits) {
                Map companyData = searchHit.getSourceAsMap();
                Company company = new Company();
                company.setName((String)companyData.get("name"));
                company.setAddress((String)companyData.get("address"));
                company.setPhone((String)companyData.get("phone"));
                company.setEmail((String)companyData.get("email"));
                company.setSite((String)companyData.get("site"));
                company.setSection((String)companyData.get("section"));
                company.setIndustry((String)companyData.get("industry"));

                results.add(company);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

}
