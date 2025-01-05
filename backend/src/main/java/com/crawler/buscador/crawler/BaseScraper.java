package com.crawler.buscador.crawler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.crawler.buscador.Exceptions.ProductNotFoundException;
import com.crawler.buscador.Exceptions.ScraperException;
import com.crawler.buscador.models.Product;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseScraper implements Scraper {
    // Método comun, crear URL
    public String buildSearchUrl(String baseUrl, String productName) {
        // Codificar el nombre del producto para que sea válido en la URL
        String endodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);
        // Combino la url base con el nombre del producto codificado
        return baseUrl + endodedProductName;
    }

    // funcion para obtener la url base
    protected abstract String getBaseUrl();

    // Creo funcion abstracta de la obtencion del HTML, extraccion de datos
    protected abstract List<Product> parseProducts(Document doc, String productName);

    @Override
    // conexionn a HTTP, conecto y obtengo el Document
    public List<Product> searchProduct(String productName) {
        List<Product> products = new ArrayList<>();
        try {
            // armo URL con url base y el nombre del producto
            log.info("Strating search for product: {}", productName);
            String searchUrl = buildSearchUrl(getBaseUrl(), productName);

            // Me conecto a la url y obtengo el HTML
            Document doc = Jsoup.connect(searchUrl).get();
            log.info("Encoded search URL_ {}", searchUrl);

            // Obtengo cada campo que necesito del html, para armar la lista de productos
            products = parseProducts(doc, productName);
            log.info("HTML contemt fetched successfully from : {}", searchUrl);

            if (products.isEmpty())
                throw new ProductNotFoundException(productName);

        } catch (IOException e) {
            log.error("Error to connecting to the website:{}", e.getMessage(), e);
            throw new ScraperException("Error connecting to the website, e");
        }
        log.info("Completed search for product: {}", productName);
        log.debug("Total products found: {}", products.size());

        return products;
    }

}
