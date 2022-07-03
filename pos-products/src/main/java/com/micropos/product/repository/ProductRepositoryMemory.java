package com.micropos.product.repository;


import com.micropos.product.model.Product;

import java.util.List;

public interface ProductRepositoryMemory {

    public List<Product> allProducts();

    public Product findProduct(String productId);

}