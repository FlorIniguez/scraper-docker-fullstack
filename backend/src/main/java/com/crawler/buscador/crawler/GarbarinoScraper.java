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
public class GarbarinoScraper implements Scraper {
    private static final String URL_BASE = "https://www.garbarino.com/shop?search=";
    private static final String URL_PRODUCT = "https://www.garbarino.com";

    @Override
    public List<Product> searchProduct(String productName) {
        log.info("Starting search for product:{}",productName);
        return scrapeGarbarino(productName);
    }

    public List<Product> scrapeGarbarino(String productName) {
        List<Product> products = new ArrayList<>();
        try {
            // Codificar el nombre del producto para que sea válido en la URL
            String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);
            //Combino la url base con el nombre del producto codificado
            String searchUrl = URL_BASE + encodedProductName;
            //Me conecto a la url y obtengo el HTML
            log.info("Encoded product search URL: {}", searchUrl);

            log.info("Connecting to URL:{}", searchUrl);
            Document doc = Jsoup.connect(searchUrl).get();
            log.info("HTML content fectched successfully from: {}", searchUrl);

            //Busca en el html la parte que quiero "traer"
            Elements productElements = doc.select("div.product-card-design6-vertical");
            //Convierto el nombre del producto en una lista de "palabras claves"
            List<String> queryWords = Arrays.asList(productName.toLowerCase().split(" "));

            //proecesar elementos, deL fragmento HTML especifico extraigo los datos que necesito
            for (Element productElement : productElements) {
                // Extraer nombre del producto, precio y enlace
                String name = productElement.select("div.product-card-design6-vertical__name").text().toLowerCase();
                String relativeLink = productElement.select("a.card-anchor").attr("href");
                String link = URL_PRODUCT + relativeLink;
                String price = productElement.select("div.product-card-design6-vertical__price span:last-child").text();
                String logo = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSlS0C8BHg2hUQLJ3nVP-sCJ1Rx0XFyMlw-1Q&s";

                //Convierto el precio en un double
                double priceDouble = ConvertPrice.convertPriceDouble(price);
                // Filtrar productos por coincidencia exacta de todas las palabras
                boolean allWordsMatch = queryWords.stream().allMatch(name::contains);

                if (allWordsMatch && !name.isEmpty() && !price.isEmpty() && !link.isEmpty()) {
                    log.debug("Product matches query: {}, Name:{}, Price:{}",name,price,link);
                    products.add(new Product(name, priceDouble, link, logo));
                }
            }

        } catch (IOException e) {
            log.error("Error during scraping process: ", e);
            throw new ScraperException("Error connecting to Garbarino", e);
        }
        log.info("Completed search for product: {}", productName);
        return products;
    }

}
