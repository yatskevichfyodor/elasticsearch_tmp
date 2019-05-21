package base.es.services;

import base.es.model.Company;
import base.es.utils.CollectionUtils;
import base.es.utils.Map2EntityConverter;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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

            SimpleQueryStringBuilder queryBuilder = new SimpleQueryStringBuilder(query);
            Map<String, Float> fieldsToSearchMap = new HashMap<>();
            fieldsToSearchMap.put("name", 3F);
            fieldsToSearchMap.put("email", 2F);
            fieldsToSearchMap.put("site", 1F);
            queryBuilder.fields(fieldsToSearchMap);

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            sourceBuilder.size(100);

            request.source(sourceBuilder);

            SearchResponse searchResponse = null;
            searchResponse = client.search(request, RequestOptions.DEFAULT);

            SearchHit[] searchHits = searchResponse.getHits().getHits();

            for (SearchHit searchHit: searchHits) {
                Map companyData = searchHit.getSourceAsMap();

                results.add(Map2EntityConverter.convertMap2Entity(companyData));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    public static List<String> findDuplicatedEmails() {
        List<String> emails = new ArrayList<>();

        SearchResponse searchResponse = null;
        try {
            SearchRequest request = new SearchRequest("companies");

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.size(100);
            sourceBuilder.aggregation(AggregationBuilders.terms("by_email")
                    .field("email")
                    .minDocCount(2)
            );

            request.source(sourceBuilder);

            searchResponse = client.search(request, RequestOptions.DEFAULT);

            ParsedStringTerms agg = searchResponse.getAggregations().get("by_email");
            List buckets = agg.getBuckets();

            for (Object bucket: buckets) {
                ParsedStringTerms.ParsedBucket b = (ParsedStringTerms.ParsedBucket)bucket;
                emails.add((String)b.getKey());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return emails;
    }

    public List<Company> searchDuplicatedCompaniesByFieldAndPrefix(String field, String prefix) {
        List<Company> results = new ArrayList<>();

        SearchResponse searchResponse = null;
        try {
            SearchRequest request = new SearchRequest("companies");

            QueryBuilder queryBuilder = new PrefixQueryBuilder(field, prefix);

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            sourceBuilder.size(100);
            sourceBuilder.aggregation(AggregationBuilders.terms("by_" + field)
                            .field(field)
//                    .minDocCount(2)
                            .subAggregation(AggregationBuilders.topHits("companies_duplicates")
                                    .sort(field)
                                    .size(100)
                            )
            );

            request.source(sourceBuilder);

            searchResponse = client.search(request, RequestOptions.DEFAULT);

            ParsedStringTerms agg = searchResponse.getAggregations().get("by_" + field);
            List buckets = agg.getBuckets();


            for (Object bucket: buckets) {
                ParsedStringTerms.ParsedBucket b = (ParsedStringTerms.ParsedBucket)bucket;
                ParsedTopHits topHits = b.getAggregations().get("companies_duplicates");
                SearchHit[] aggregationHits = topHits.getHits().getHits();

                for (SearchHit searchHit : aggregationHits) {
                    Map companyData = searchHit.getSourceAsMap();

                    results.add(Map2EntityConverter.convertMap2Entity(companyData));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    /** Идет {criteria.size()} поисковых запросов на поиск дубликатов по 1 полю
     *  потом сдери этих результатов ищутся общие и возвращаются
     */
    public List<Company> searchDuplicatedCompaniesByComplicatedCriteria(List<Map<String, String>> criteria) {
        List<Company> results = new ArrayList<>();

        List<List<Company>> aggregationsResults = new ArrayList<>();
        SearchResponse searchResponse = null;
        try {
            for (Map<String, String> cr : criteria) {
                String fieldname = cr.get("fieldname");
                String value = cr.get("value");
                if (value == null) {
                    value = "";
                }
                aggregationsResults.add(searchDuplicatedCompaniesByFieldAndPrefix(fieldname, value));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        results = CollectionUtils.intersection(aggregationsResults);

//        results.sort(Comparator.comparing(Company::getEmail).thenComparing(Company::getSite));

        return results;
    }
}
