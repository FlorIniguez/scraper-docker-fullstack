package com.crawler.buscador.crawler;

import com.crawler.buscador.Exceptions.ProductNotFoundException;
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
import java.util.Objects;

@Component
@Slf4j
public class MlibreScraper implements Scraper {
    private static final String URL_BASE = "https://listado.mercadolibre.com.ar/";

    @Override
    public List<Product> searchProduct(String productName) {
        log.info("Starting search for product: {}", productName);
        try {
            return scrapeMLibre(productName);
        } catch (Exception e) {
            log.error("Error scraping MercadoLibre for product: {}", productName, e);
            throw new ScraperException("Error scraping MercadoLibre for product: " + productName, e);
        }
    }

    public List<Product> scrapeMLibre(String productName) {
        List<Product> products = new ArrayList<>();
        try {
            // Codifica nombre del producto para que sea válido en la URL
            String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);
            // Une la URL base con el procurtName codificado
            String searchUrl = URL_BASE + encodedProductName + "#D[A:" + encodedProductName + "]";
            log.info("Encoded product search URL: {}", searchUrl);

            log.info("Connecting to URL:{}", searchUrl);
            // Conecta a la URL para acceder al html
            Document doc = Jsoup.connect(searchUrl).get();
            log.info("HTML content fectched successfully from: {}", searchUrl);
            // Trae los fragmentos de HTML donde estan los productos
            Elements productElements = doc.select("li.ui-search-layout__item");
             // Log de cantidad de elementos encontrados en la página
             log.debug("Found {} product elements", productElements.size());
            // Convierte el product name en una lista de palabras
            List<String> queryWords = Arrays.asList(productName.toLowerCase().split(" "));

            for (Element productElement : productElements) {
                // Extraer nombre del producto, precio y enlace
                String name = productElement.select("h2.poly-box a").text().toLowerCase();
                String link = productElement.select("h2.poly-box a").attr("href");
                String price = Objects
                        .requireNonNull(productElement.select("span.andes-money-amount__fraction").first()).text();
                String logo = "https://http2.mlstatic.com/static/org-img/homesnw/mercado-libre.png?v=2";

                // Convertir precio a double
                double priceDouble = ConvertPrice.convertPriceDouble(price);
                // Corrobora si las palabras del productName (lista) coinciden con el nombre del
                // producto
                boolean allWordsMatch = queryWords.stream().allMatch(name::contains);
                // Verificacion de que este toda la info que necesito
                if (allWordsMatch && !name.isEmpty() && !price.isEmpty() && !link.isEmpty()) {
                    log.debug("Product matches query: {}, Name:{}, Price:{}", name, price, link);
                    products.add(new Product(name, priceDouble, link, logo));
                }
            }
              // Si no se encontraron productos, lanzar la excepción
              if (products.isEmpty()) {
                log.error("No products found for the query: {}", productName);
                throw new ProductNotFoundException(productName);
            }

        } catch (IOException e) {
            log.error("Error during scraping process:", e);
            throw new ScraperException("Error connecting to Mercado Libre", e);
        }
        // Log final de la búsqueda
        log.info("Completed search for product: {}", productName);
        log.debug("Total products found: {}", products.size());
        return products;
    }
}
