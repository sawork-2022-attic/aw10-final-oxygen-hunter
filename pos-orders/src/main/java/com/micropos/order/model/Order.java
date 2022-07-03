package com.micropos.order.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Document("orders")
@Accessors(fluent = true, chain = true)
public class Order implements Serializable {

    @Id
    @Setter
    @Getter
    private String id;

    @Setter
    @Getter
    private String time;

    @Setter
    @Getter
    private List<Item> items = new ArrayList<>();

}
