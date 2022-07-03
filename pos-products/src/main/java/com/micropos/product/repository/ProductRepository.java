package com.micropos.product.repository;

import com.micropos.product.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

    Flux<Product> findBy(Pageable pageable);

    Flux<Product> findByNameLike(String name, Pageable pageable);
}
