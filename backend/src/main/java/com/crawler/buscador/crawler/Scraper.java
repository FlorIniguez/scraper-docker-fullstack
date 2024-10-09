package com.crawler.buscador.crawler;

import com.crawler.buscador.models.Product;

import java.util.List;

public interface Scraper {
    List<Product> searchProduct(String productName);
}
