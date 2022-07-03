package com.micropos.cart.service;


import com.micropos.cart.mapper.CartMapper;
import com.micropos.cart.model.Cart;
import com.micropos.cart.model.Item;
import com.micropos.cart.repository.CartRepository;
import com.micropos.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CartServiceImpl implements CartService {

    private CartRepository cartRepository;

    private final String COUNTER_URL = "http://POS-COUNTER/counter/";

    private final String ORDER_URL = "http://POS-ORDERS/api/orders/";

    private CartMapper cartMapper;

    @Autowired
    public void setCartMapper(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    @LoadBalanced
    private WebClient webClient;

    @Autowired
    void setWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Autowired
    public void setCartRepository(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Mono<Cart> createCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public Mono<Cart> addItem(String cartId, Item item) {
        return cartRepository.findById(cartId)
                .flatMap(cart -> {
                    cart.addItem(item);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Mono<Cart> getCart(String cartId) {
        return cartRepository.findById(cartId);
    }

    @Override
    public Flux<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Override
    public Mono<Double> getTotal(String cartId) {
        return cartRepository.findById(cartId)
                .flatMap(this::getTotal);
    }

    @Override
    public Mono<OrderDto> checkout(String cartId) {
        return cartRepository.findById(cartId)
                .flatMap(cart -> {
                    Mono<OrderDto> orderDtoMono = webClient.post()
                            .uri(ORDER_URL)
                            .bodyValue(cartMapper.toCartDto(cart))
                            .retrieve()
                            .toEntity(OrderDto.class)
                            .map(HttpEntity::getBody);
                    cartRepository.deleteById(cartId).subscribe();
                    return orderDtoMono;
                });
    }

    private Mono<Double> getTotal(Cart cart) {
        return webClient
                .post()
                .uri(COUNTER_URL + "/checkout")
                .bodyValue(cartMapper.toCartDto(cart))
                .retrieve()
                .toEntity(Double.class)
                .map(HttpEntity::getBody);
    }
}