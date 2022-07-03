package com.micropos.order.rest;

import com.micropos.api.OrdersApi;
import com.micropos.dto.CartDto;
import com.micropos.dto.OrderDto;
import com.micropos.order.mapper.OrderMapper;
import com.micropos.order.model.Order;
import com.micropos.order.service.OrderService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api")
public class OrderController implements OrdersApi {

    private final OrderMapper orderMapper;

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @Override
    public Mono<ResponseEntity<OrderDto>> createOrder(Mono<CartDto> cartDto, ServerWebExchange exchange) {
        return orderService.createOrder(cartDto).map(orderMapper::toOrderDto).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<OrderDto>>> listOrders(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(orderService.getAllOrders().map(orderMapper::toOrderDto)));
    }

    @Override
    public Mono<ResponseEntity<OrderDto>> showOrderById(String orderId, ServerWebExchange exchange) {
        return orderService.getOrder(orderId)
                .map(orderMapper::toOrderDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
