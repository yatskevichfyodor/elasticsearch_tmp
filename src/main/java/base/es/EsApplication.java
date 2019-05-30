package base.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class EsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsApplication.class, args);
    }
}
