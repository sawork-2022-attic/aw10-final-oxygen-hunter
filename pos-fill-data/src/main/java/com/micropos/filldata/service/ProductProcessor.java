package com.micropos.filldata.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.micropos.filldata.model.Product;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductProcessor implements ItemProcessor<JsonNode, Product> {

    private final Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    @Override
    public Product process(@NonNull JsonNode jsonNode) {
        String name = jsonNode.get("title").asText();
        JsonNode image = jsonNode.get("imageURLHighRes");
        String imageUrl = "";
        if (image.isArray() && image.size() > 0) {
            imageUrl = image.get(0).asText();
        }
        String priceString = jsonNode.get("price").asText();
        Matcher match = pattern.matcher(priceString);
        if (match.find()) {
            double price = Double.parseDouble(match.group(1));
            return new Product(name, price, imageUrl);
        }
        return null;
    }
}
