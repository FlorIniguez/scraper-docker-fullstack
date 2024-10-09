package com.crawler.buscador.crawler;

import com.crawler.buscador.Exceptions.ProductNotFoundException;
import com.crawler.buscador.Exceptions.ScraperException;
import com.crawler.buscador.models.Product;
import com.crawler.buscador.utils.ConvertPrice;
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
public class GarbarinoScraper implements Scraper {
    private static final String URL_BASE = "https://www.garbarino.com/shop?search=";
    private static final String URL_PRODUCT = "https://www.garbarino.com";

    @Override
    public List<Product> searchProduct(String productName) {
        return scrapeGarbarino(productName);
    }

    public List<Product> scrapeGarbarino(String productName) {
        List<Product> products = new ArrayList<>();
        try {
            // Codificar el nombre del producto para que sea válido en la URL
            String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);
            String searchUrl = URL_BASE + encodedProductName;
            Document doc = Jsoup.connect(searchUrl).get();
            Elements productElements = doc.select("div.product-card-design6-vertical");

            List<String> queryWords = Arrays.asList(productName.toLowerCase().split(" "));

            //proecesar elementos
            for (Element productElement : productElements) {
                // Extraer nombre del producto, precio y enlace
                String name = productElement.select("div.product-card-design6-vertical__name").text().toLowerCase();
                String relativeLink = productElement.select("a.card-anchor").attr("href");
                String link = URL_PRODUCT + relativeLink;
                String price = productElement.select("div.product-card-design6-vertical__price span:last-child").text();
                String logo = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSlS0C8BHg2hUQLJ3nVP-sCJ1Rx0XFyMlw-1Q&s";

                double priceDouble = ConvertPrice.convertPriceDouble(price);
                // Filtrar productos por coincidencia exacta de todas las palabras
                boolean allWordsMatch = queryWords.stream().allMatch(name::contains);

                if (allWordsMatch && !name.isEmpty() && !price.isEmpty() && !link.isEmpty()) {
                    products.add(new Product(name, priceDouble, link, logo));
                }
            }

        } catch (IOException e) {
            throw new ScraperException("Error connecting to Garbarino", e);
        }
        return products;
    }

}
