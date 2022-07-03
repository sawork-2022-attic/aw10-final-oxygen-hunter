package com.micropos.filldata.repository;

import com.micropos.filldata.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProductRepository extends MongoRepository<Product, String> {

}
