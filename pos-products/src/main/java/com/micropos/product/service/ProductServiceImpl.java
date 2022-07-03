package com.micropos.product.service;

import com.micropos.product.model.Product;
import com.micropos.product.repository.ProductRepository;
import com.micropos.product.repository.ProductRepositoryMemory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    private ProductRepositoryMemory productRepositoryMemory;

    private static final int PAGE_SIZE = 10;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setProductRepositoryMemory(ProductRepositoryMemory productRepositoryMemory) {
        this.productRepositoryMemory = productRepositoryMemory;
    }

    @Override
    public Flux<Product> products(Optional<Integer> page, Optional<String> keyword) {
//        List<Product> products =  productRepositoryMemory.allProducts();
//        return Flux.fromIterable(products);
        PageRequest request = PageRequest.of(page.orElse(0), PAGE_SIZE);
        Flux<Product> result;
        if (keyword.isEmpty()) {
            result = productRepository.findBy(request);
        } else {
            result = productRepository.findByNameLike(keyword.get(), request);
        }
        return result;
    }

    @Override
    public Mono<Product> getProduct(String id) {
        return productRepository.findById(id);
    }

    private void generateData(String keyword) {
        try {
            List<Product> products = parseJD(keyword);
            productRepository.saveAll(products);
        } catch (Exception e) {
            System.out.println("exception");
        }
    }

    private static List<Product> parseJD(String keyword) throws IOException {
        String url = "https://search.jd.com/Search?keyword=" + keyword;
        Document document = Jsoup.parse(new URL(url), 10000);
        Element element = document.getElementById("J_goodsList");
        Elements elements = element.getElementsByTag("li");
        List<Product> list = new ArrayList<>();

        for (Element el : elements) {
            String id = el.attr("data-spu");
            String img = "https:".concat(el.getElementsByTag("img").eq(0).attr("data-lazy-img"));
            String price = el.getElementsByAttribute("data-price").text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            if (title.indexOf("，") >= 0)
                title = title.substring(0, title.indexOf("，"));

            Product product = new Product(id, title, Double.parseDouble(price), img);
            list.add(product);
        }
        return list;
    }
}
