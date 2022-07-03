package com.micropos.delivery.repository;

import com.micropos.delivery.model.Entry;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.CrudRepository;
import reactor.core.publisher.Mono;

public interface DeliveryRepository extends ReactiveMongoRepository<Entry, String> {

    Mono<Entry> findByOrderId(String orderId);
}
