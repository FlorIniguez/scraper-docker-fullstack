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
import java.util.Objects;

@Component
@Slf4j
public class MlibreScraper extends BaseScraper {
    private static final String URL_BASE = "https://listado.mercadolibre.com.ar/";
    private static final String LOGO_ML = "https://http2.mlstatic.com/static/org-img/homesnw/mercado-libre.png?v=2";

    @Override
    protected String getBaseUrl() {
        return URL_BASE;
    }

    @Override
    protected List<Product> parseProducts(Document doc, String productName) {
        List<Product> products = new ArrayList<>();
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
            String logo = LOGO_ML;

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
        return products;
    }

}
