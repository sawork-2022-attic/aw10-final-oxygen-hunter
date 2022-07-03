package com.micropos.order.service;

import com.micropos.dto.CartDto;
import com.micropos.dto.CartItemDto;
import com.micropos.dto.OrderDto;
import com.micropos.order.mapper.OrderMapper;
import com.micropos.order.model.Item;
import com.micropos.order.model.Order;
import com.micropos.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private OrderRepository orderRepository;

    private OrderMapper orderMapper;

    private StreamBridge streamBridge;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Autowired
    public void setOrderMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Autowired
    public void setStreamBridge(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public Mono<Order> createOrder(Mono<CartDto> cartDto) {
        return cartDto.map(cart -> {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = df.format(new Date());
                    Order order = new Order();
                    order.time(time);
                    List<Item> items = new ArrayList<>();
                    for (CartItemDto cartItem : cart.getItems()) {
                        items.add(new Item().id(null)
                                .productId(cartItem.getProduct().getId())
                                .productName(cartItem.getProduct().getName())
                                .unitPrice(cartItem.getProduct().getPrice())
                                .image(cartItem.getProduct().getImage())
                                .quantity(cartItem.getAmount()));
                    }
                    order.items(items);
                    return order;
                }).flatMap(orderRepository::save)
                .map(order -> {
                    log.info("send {}", orderMapper.toOrderDto(order).toString());
                    streamBridge.send("order-send", orderMapper.toOrderDto(order));
                    return order;
                });
    }

    @Override
    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Mono<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }

    private void sendOrder(Order order) {
        log.info("send {}", orderMapper.toOrderDto(order).toString());
        streamBridge.send("order-send", orderMapper.toOrderDto(order));
    }

}
