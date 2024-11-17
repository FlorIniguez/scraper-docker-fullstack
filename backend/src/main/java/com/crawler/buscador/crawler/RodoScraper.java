package com.crawler.buscador.crawler;

import com.crawler.buscador.Exceptions.ScraperException;
import com.crawler.buscador.models.Product;
import com.crawler.buscador.utils.ConvertPrice;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class RodoScraper implements Scraper {
    private static final String BASE_URL = "https://www.rodo.com.ar/catalogsearch/result/?q=";

    @Override
    public List<Product> searchProduct(String productName) {
        log.info("Starting search for product:{}",productName);
        return scrapeRodo(productName);
    }

    public List<Product> scrapeRodo(String productName) {
        List<Product> products = new ArrayList<>();
        try {
            String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);
            String searchUrl = BASE_URL + encodedProductName;
            log.info("Encoded product search URL: {}", searchUrl);
            // Conectar y obtener el documento
            log.info("Connecting to URL:{}", searchUrl);
            Document doc = Jsoup.connect(searchUrl).get();
            Elements productElements = doc.select("li.products");
            //convierto productname de un array a una lista
            List<String> queryWords = Arrays.asList(productName.toLowerCase().split(" "));

            // Procesar los elementos del producto
            for (Element productElement : productElements) {
                String name = productElement.select("strong.product-item-name a").text().toLowerCase();
                String link = productElement.select("strong.product-item-name a").attr("href");
                String price = productElement.select("span.price-wrapper span.price").text();
                String logo = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTslpiN9il4_Wye9HwyinGLT55w3X6adCsN5g&s";

                double priceDouble = ConvertPrice.convertPriceDouble(price);
                boolean allWordsMatch = queryWords.stream().allMatch(name::contains);

                if (allWordsMatch && !name.isEmpty() && !price.isEmpty() && !link.isEmpty()) {
                    log.debug("Product matches query: {}, Name:{}, Price:{}",name,price,link);
                    products.add(new Product(name, priceDouble, link, logo));
                }
            }
        } catch (IOException e) {
            log.error("Error during scraping process: ", e);
            throw new ScraperException("Error connecting to Rodo", e);
        }
        return products;
    }
}