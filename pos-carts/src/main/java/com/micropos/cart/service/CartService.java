package com.micropos.cart.service;

import com.micropos.cart.model.Cart;
import com.micropos.cart.model.Item;
import com.micropos.dto.OrderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface CartService {

    Mono<Cart> createCart(Cart cart);

    Mono<Cart> addItem(String cartId, Item item);

    Mono<Cart> getCart(String cartId);

    Flux<Cart> getAllCarts();

    Mono<Double> getTotal(String cartId);

    Mono<OrderDto> checkout(String cartId);

}