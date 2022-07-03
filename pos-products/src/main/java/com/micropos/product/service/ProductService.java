package com.micropos.product.service;

import com.micropos.product.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Flux<Product> products(Optional<Integer> page, Optional<String> keyword);

    Mono<Product> getProduct(String id);

}
