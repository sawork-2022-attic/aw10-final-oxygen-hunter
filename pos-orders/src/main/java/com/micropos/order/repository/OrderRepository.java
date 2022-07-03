package com.micropos.order.repository;

import com.micropos.order.model.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
}
