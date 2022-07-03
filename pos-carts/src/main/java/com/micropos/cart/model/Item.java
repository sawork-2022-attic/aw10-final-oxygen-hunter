package com.micropos.cart.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Accessors(fluent = true)
public class Item {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String productId;

    @Getter
    @Setter
    private String productName;

    @Getter
    @Setter
    private double unitPrice;

    @Getter
    @Setter
    private String image;

    @Getter
    @Setter
    private int quantity;
}
