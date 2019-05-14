package base.es;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Controller
public class EsApplication {

    static int id = 0;

    public static void main(String[] args) {
        SpringApplication.run(EsApplication.class, args);
    }

    RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            )
    );

    @GetMapping("/index")
    @ResponseStatus(HttpStatus.OK)
    public void index() {
        System.out.println("- index method started");

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "galantis");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest request = new IndexRequest("posts")
                .id(String.valueOf(id))
                .source(jsonMap)
                .opType("create");
        IndexResponse response;
        try {
            response = client.index(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            if (e.status() == RestStatus.CONFLICT) {
                System.out.println("if (e.status() == RestStatus.CONFLICT)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        id++;
        System.out.println("tmp");
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public void search() {
        System.out.println("- search method started");

        SearchRequest request = new SearchRequest("posts");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("message", "trying out Elasticsearch"));

        request.source(sourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            if (e.status() == RestStatus.CONFLICT) {
                System.out.println("if (e.status() == RestStatus.CONFLICT)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("- search method finished");
    }

    @GetMapping("/tst")
    @ResponseStatus(HttpStatus.OK)
    public void tst() {
        System.out.println("- tst method started");

    }

}
