package com.micropos.filldata.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.micropos.filldata.model.Product;
import com.micropos.filldata.repository.ProductRepository;
import com.micropos.filldata.service.JsonFileReader;
import com.micropos.filldata.service.ProductProcessor;
import com.micropos.filldata.service.ProductWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final ProductRepository productRepository;

    @Autowired
    public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ProductRepository productRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.productRepository = productRepository;
    }

    @Bean
    public Job partitioningJob() throws IOException {
        return jobBuilderFactory
                .get("partitioningJob")
                .incrementer(new RunIdIncrementer())
                .start(masterStep())
                .build();
    }

    @Bean
    public Step masterStep() throws IOException {
        return stepBuilderFactory
                .get("masterStep")
                .partitioner("slaveStep", partitioner())
                .step(slaveStep())
                .build();
    }

    @Bean
    public Step slaveStep() throws FileNotFoundException {
        return stepBuilderFactory
                .get("slaveStep")
                .<JsonNode, Product>chunk(10000)
                .reader(itemReader(null))
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();

    }

    public Partitioner partitioner() throws IOException {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:/data/*.json");
        partitioner.setResources(resources);
        //partitioner.setResources(resolver.getResources("file:src/main/resources/data/*.json"));
        return partitioner;
    }

    @Bean
    @StepScope
    public ItemReader<JsonNode> itemReader(@Value("#{stepExecutionContext['fileName']}") String filename) throws FileNotFoundException {
        return new JsonFileReader(filename);
    }

    @Bean
    public ItemProcessor<JsonNode, Product> itemProcessor() {
        return new ProductProcessor();
    }

    @Bean
    public ProductWriter itemWriter(){
        return new ProductWriter(productRepository);
    }
}
