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
public class GarbarinoScraper extends BaseScraper {
    private static final String URL_BASE = "https://www.garbarino.com/shop?search=";
    private static final String URL_PRODUCT = "https://www.garbarino.com";
    private static final String LOGO_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSlS0C8BHg2hUQLJ3nVP-sCJ1Rx0XFyMlw-1Q&s";

    @Override
    //Sobreescribo el metodo
    protected String getBaseUrl() {
        return URL_BASE;
    }
    @Override
    protected List<Product> parseProducts(Document doc, String productName) {
        List <Product> products = new ArrayList<>();
          // Busca en el html la parte que quiero "traer"
          Elements productElements = doc.select("div.product-card-design6-vertical");
          // Log de cantidad de elementos encontrados en la página
          log.debug("Found {} product elements", productElements.size());
          // Convierto el nombre del producto en una lista de "palabras claves"
          List<String> queryWords = Arrays.asList(productName.toLowerCase().split(" "));
          
          // proecesar elementos, deL fragmento HTML especifico extraigo los datos que Fnecesito
          for (Element productElement : productElements) {
              // Extraer nombre del producto, precio y enlace
              String name = productElement.select("div.product-card-design6-vertical__name").text().toLowerCase();
              String relativeLink = productElement.select("a.card-anchor").attr("href");
              String link = URL_PRODUCT + relativeLink;
              String price = productElement.select("div.product-card-design6-vertical__price span:last-child").text();
              String logo = LOGO_URL;

              // Convierto el precio en un double
              double priceDouble = ConvertPrice.convertPriceDouble(price);
              // Filtrar productos por coincidencia exacta de todas las palabras
              boolean allWordsMatch = queryWords.stream().allMatch(name::contains);

              if (allWordsMatch && !name.isEmpty() && !price.isEmpty() && !link.isEmpty()) {
                  log.debug("Product matches query: {}",productName);
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
