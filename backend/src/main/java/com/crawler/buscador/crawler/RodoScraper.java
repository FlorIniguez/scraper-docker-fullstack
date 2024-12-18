package com.crawler.buscador.crawler;

import com.crawler.buscador.Exceptions.ProductNotFoundException;
import com.crawler.buscador.models.Product;
import com.crawler.buscador.utils.ConvertPrice;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class RodoScraper extends BaseScraper {
    private static final String BASE_URL = "https://www.rodo.com.ar/catalogsearch/result/?q=";

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    protected List<Product> parseProducts(Document doc, String productName) {
        List<Product> products = new ArrayList<>();
        Elements productElements = doc.select("li.products");
        // Log de cantidad de elementos encontrados en la página
        log.debug("Found {} product elements", productElements.size());
        // convierto productname a una lista
        List<String> queryWords = Arrays.asList(productName.toLowerCase().split(" "));

        // Procesar los elementos del producto
        for (Element productElement : productElements) {
            String name = productElement.select("strong.product-item-name a").text().toLowerCase();
            String link = productElement.select("strong.product-item-name a").attr("href");
            String price = productElement.select("span.price-wrapper span.price").text();
            String logo = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTslpiN9il4_Wye9HwyinGLT55w3X6adCsN5g&s";

            // convierte precio a double para poder compararlos
            double priceDouble = ConvertPrice.convertPriceDouble(price);
            // Si la lista de palabras coincide con el nombre del producto
            boolean allWordsMatch = queryWords.stream().allMatch(name::contains);

            if (allWordsMatch && !name.isEmpty() && !price.isEmpty() && !link.isEmpty()) {
                log.debug("Found {} product elements", productElements.size());
                log.debug("Found product: Name: {}, Price: {}", name, price);

                products.add(new Product(name, priceDouble, link, logo));
            }
        }
        // Si no se encontraron productos, lanzar la excepción
        if (products.isEmpty()) {
            log.error("No products found for the query: {}", productName);
            throw new ProductNotFoundException(productName);
        }
        return products;

    }

}