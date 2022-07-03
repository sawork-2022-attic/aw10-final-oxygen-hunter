package com.micropos.counter.web;

import com.micropos.api.CounterApi;
import com.micropos.counter.service.CounterService;
import com.micropos.dto.CartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class CounterController implements CounterApi {

    private CounterService counterService;

    @Autowired
    public void setCounterService(CounterService counterService) {
        this.counterService = counterService;
    }

    @Override
    public Mono<ResponseEntity<Double>> checkout(Mono<CartDto> cartDto, ServerWebExchange exchange) {
        return cartDto.map(counterService::getTotal)
                .map(ResponseEntity::ok);
    }
}
