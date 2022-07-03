package com.micropos.cart.mapper;

import com.micropos.cart.model.Cart;
import com.micropos.cart.model.Item;
import com.micropos.dto.CartDto;
import com.micropos.dto.CartItemDto;
import com.micropos.dto.ProductDto;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper
public interface CartMapper {
    Collection<CartDto> toCartDtos(Collection<Cart> carts);

    Collection<Cart> toCarts(Collection<CartDto> cartDtos);

    default Cart toCart(CartDto cartDto) {
        return new Cart().id(cartDto.getId())
                .items(toItems(cartDto.getItems()));

    }

    default CartDto toCartDto(Cart cart) {
        return new CartDto().id(cart.id())
                .items(toItemDtos(cart.items()));
    }


    default List<CartItemDto> toItemDtos(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        List<CartItemDto> list = new ArrayList<>(items.size());
        for (Item item : items) {
            list.add(toItemDto(item));
        }

        return list;
    }

    default List<Item> toItems(List<CartItemDto> itemDtos) {
        if (itemDtos == null || itemDtos.isEmpty()) {
            return null;
        }
        List<Item> list = new ArrayList<>(itemDtos.size());
        for (CartItemDto itemDto : itemDtos) {
            list.add(toItem(itemDto));
        }

        return list;
    }

    default CartItemDto toItemDto(Item item) {
        return new CartItemDto()
                .amount(item.quantity())
                .product(getProductDto(item));
    }

    default Item toItem(CartItemDto itemDto) {
        return new Item()
                .productId(itemDto.getProduct().getId())
                .productName(itemDto.getProduct().getName())
                .quantity(itemDto.getAmount())
                .unitPrice(itemDto.getProduct().getPrice())
                .image(itemDto.getProduct().getImage());
    }

    default ProductDto getProductDto(Item item) {
        return new ProductDto().id(item.productId())
                .name(item.productName())
                .price(item.unitPrice())
                .image(item.image());
    }

}
