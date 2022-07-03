package com.micropos.delivery.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("delivery_entries")
@Accessors(fluent = true, chain = true)
public class Entry implements Serializable {
    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String orderId;

    @Getter
    @Setter
    private String status;

    public String toString() {
        return "Delivery Entry: {id: " + id + ", orderId: " + orderId + ", status: " + status + "}";
    }
}
