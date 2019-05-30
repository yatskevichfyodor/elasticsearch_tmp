package base.es.services;

import base.es.model.Contractor;
import base.es.utils.Map2EntityConverter;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ContractorService {

    private static int id = 0;
    private static String indexName = "contractors";

    private static RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            )
    );

    public void index(Contractor contractor) {
        Map<String, Object> jsonMap = new HashMap<>();

        jsonMap.put("INN", contractor.getINN());
        jsonMap.put("KPP", contractor.getKPP());
        jsonMap.put("phone", contractor.getPhone());

        jsonMap.put("code1", contractor.getCode1());
        jsonMap.put("name", contractor.getName());
        jsonMap.put("nameWorking", contractor.getNameWorking());
        jsonMap.put("nameByDocuments", contractor.getNameByDocuments());
        jsonMap.put("legalAddress", contractor.getLegalAddress());
        jsonMap.put("actualAddress", contractor.getActualAddress());
        jsonMap.put("mainBankAccount", contractor.getMainBankAccount());

        jsonMap.put("code2", contractor.getCode2());
        jsonMap.put("shortName", contractor.getShortName());
        jsonMap.put("ownershipType", contractor.getOwnershipType());
        jsonMap.put("fullName", contractor.getFullName());
        jsonMap.put("address", contractor.getAddress());
        jsonMap.put("holding", contractor.getHolding());

        IndexRequest request = new IndexRequest(indexName)
                .id(String.valueOf(id))
                .source(jsonMap)
                .opType("create");

        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        id++;
    }

    public List<Contractor> searchDuplicatesByFieldAndPrefix(String field, String prefix) {
        List<Contractor> results = new ArrayList<>();

        SearchResponse searchResponse = null;
        try {
            SearchRequest request = new SearchRequest(indexName);

            QueryBuilder queryBuilder = new PrefixQueryBuilder(field, prefix);

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            sourceBuilder.size(100);
            sourceBuilder.aggregation(AggregationBuilders.terms("by_" + field)
                    .field(field)
                    .minDocCount(2)
                    .subAggregation(AggregationBuilders.topHits(indexName + "_duplicates")
                            .sort(field)
                            .size(100)
                    )
            );

            request.source(sourceBuilder);

            searchResponse = client.search(request, RequestOptions.DEFAULT);

            ParsedStringTerms agg = searchResponse.getAggregations().get("by_" + field);
            List buckets = agg.getBuckets();

            List<SearchHit> allSearchHits = new ArrayList<>();

            for (Object bucket: buckets) {
                ParsedStringTerms.ParsedBucket b = (ParsedStringTerms.ParsedBucket)bucket;
                ParsedTopHits topHits = b.getAggregations().get(indexName + "_duplicates");
                allSearchHits.addAll(Arrays.asList(topHits.getHits().getHits()));


            }

            List<SearchHit> uniqueHits = allSearchHits.stream().distinct().collect(Collectors.toList());

            for (SearchHit searchHit : uniqueHits) {
                Map entityData = searchHit.getSourceAsMap();

                results.add(Map2EntityConverter.convertMap2Contractor(entityData));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    /** Составляется сложный поисковой запрос с вложенными агрегациями
     */
    public List<Contractor> searchDuplicates(List<Map<String, String>> criteria) {
        List<Contractor> results = new ArrayList<>();

        SearchResponse searchResponse = null;

        List<Map<String, String>> reversedCriteria = new ArrayList<>(criteria);
        Collections.reverse(reversedCriteria);

        AggregationBuilder rootAggregation = composeMultilevelAggregation(reversedCriteria);
        BoolQueryBuilder queryBuilder = composeBoolQueryBuilder(reversedCriteria);

        try {
            SearchRequest request = new SearchRequest(indexName);

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.size(0);
            sourceBuilder.aggregation(rootAggregation);
            sourceBuilder.query(queryBuilder);
            request.source(sourceBuilder);

            searchResponse = client.search(request, RequestOptions.DEFAULT);

            Aggregations agg = searchResponse.getAggregations();

            List<SearchHit> topHits = getAggregationTopHits(agg, criteria);

            List<SearchHit> uniqueHits = topHits.stream().distinct().collect(Collectors.toList());

            for (SearchHit searchHit : uniqueHits) {
                Map entityData = searchHit.getSourceAsMap();

                results.add(Map2EntityConverter.convertMap2Contractor(entityData));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    private static AggregationBuilder composeMultilevelAggregation(List<Map<String, String>> criteria) {
        AggregationBuilder aggregation = null;

        for (Map<String, String> cr : criteria) {
            String fieldname = cr.get("fieldname");
            AggregationBuilder tempAggregation = aggregation;

            aggregation = AggregationBuilders.terms("by_" + fieldname)
                    .field(fieldname)
                    .minDocCount(2)
            ;

            if (tempAggregation != null) {
                aggregation.subAggregation(tempAggregation);
            } else {
                aggregation.subAggregation(AggregationBuilders.topHits(indexName + "_duplicates")
                        .size(100)
                );
            }

        }

        return aggregation;
    }

    private static BoolQueryBuilder composeBoolQueryBuilder(List<Map<String, String>> criteria) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();

        for (Map<String, String> cr : criteria) {
            String fieldname = cr.get("fieldname");
            String value = cr.get("value");
            if (value == null) {
                value = "";
            }

            queryBuilder.must(new PrefixQueryBuilder(fieldname, value));
        }

        return queryBuilder;
    }

    private static List<SearchHit> getAggregationTopHits(Aggregations aggregations, List<Map<String, String>> criteria) {
        List<SearchHit> results = new ArrayList<>();

        List<Aggregation> tempAggregations = null;

        for (Map<String, String> cr : criteria) {
            String fieldname = cr.get("fieldname");
            String value = cr.get("value");
            if (value == null) {
                value = "";
            }

            if (tempAggregations == null) {
                ParsedStringTerms agg = aggregations.get("by_" + fieldname);
                List buckets = agg.getBuckets();

                List<Aggregation> subAggregations = new ArrayList<>();
                for (Object b : buckets) {
                    ParsedStringTerms.ParsedBucket bucket = (ParsedStringTerms.ParsedBucket) b;
                    subAggregations.addAll(bucket.getAggregations().asList());
                }

                tempAggregations = subAggregations;
            } else {
                List<Aggregation> subAggregations = new ArrayList<>();

                for (Aggregation aggregation: tempAggregations) {
                    if (aggregation instanceof ParsedStringTerms) {
                        ParsedStringTerms agg = (ParsedStringTerms) aggregation;
                        List buckets = agg.getBuckets();

                        for (Object b : buckets) {
                            ParsedStringTerms.ParsedBucket bucket = (ParsedStringTerms.ParsedBucket) b;
                            subAggregations.addAll(bucket.getAggregations().asList());
                        }
                    }
                }

                tempAggregations = subAggregations;
            }
        }

        if (tempAggregations == null) {
            throw new RuntimeException();
        }

        List<Aggregation> topLevelAggregations = new ArrayList<>(tempAggregations);

        for (Aggregation aggregation: topLevelAggregations) {
            if (aggregation instanceof ParsedTopHits) {
                ParsedTopHits topHitsAggregation = (ParsedTopHits) aggregation;
                SearchHit[] aggregationHits = topHitsAggregation.getHits().getHits();
                results.addAll(Arrays.asList(aggregationHits));
            } else {
                throw new RuntimeException();
            }
        }

        return results;
    }
}
