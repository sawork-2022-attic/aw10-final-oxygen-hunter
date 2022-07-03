package com.micropos.filldata;

import com.micropos.filldata.repository.ProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
public class FillDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(FillDataApplication.class, args);
    }

}
