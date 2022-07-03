package com.micropos.delivery.service;

import com.micropos.delivery.model.Entry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeliveryService {

    Flux<Entry> getAllEntries();

    Mono<Entry> getEntryByOrderId(String orderId);
}
