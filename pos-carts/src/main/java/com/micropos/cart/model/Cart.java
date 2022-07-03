package com.micropos.cart.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Document("carts")
@Accessors(fluent = true, chain = true)
public class Cart implements Serializable {

    @Id
    @Getter
    @Setter
    private String id;

    @Setter
    @Getter
    private List<Item> items = new ArrayList<>();

    public boolean addItem(Item item) {
        // items.id can not conflict
        return items.add(item);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

}
