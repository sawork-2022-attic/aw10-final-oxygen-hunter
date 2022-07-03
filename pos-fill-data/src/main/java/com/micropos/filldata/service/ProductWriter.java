package com.micropos.filldata.service;

import com.micropos.filldata.model.Product;
import com.micropos.filldata.repository.ProductRepository;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class ProductWriter implements ItemWriter<Product> {

    private final ProductRepository repository;

    public ProductWriter(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(List<? extends Product> list) {
        repository.saveAll(list);
    }
}
