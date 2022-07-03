package com.micropos.delivery.service;

import com.micropos.delivery.model.Entry;
import com.micropos.delivery.repository.DeliveryRepository;
import com.micropos.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.function.Consumer;

@Service
public class DeliveryServiceImpl implements DeliveryService{



    private DeliveryRepository deliveryRepository;

    @Autowired
    public void setDeliveryRepository(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public Flux<Entry> getAllEntries() {
        return deliveryRepository.findAll();
    }

    @Override
    public Mono<Entry> getEntryByOrderId(String orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }
}
