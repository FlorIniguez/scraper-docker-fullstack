package com.crawler.buscador.crawler;

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
import java.util.Objects;

@Component
public class MlibreScraper implements Scraper {
    private static final String URL_BASE = "https://listado.mercadolibre.com.ar/";

    @Override
    public List<Product> searchProduct(String productName) {
        return scrapeMLibre(productName);
    }

    public List<Product> scrapeMLibre(String productName) {
        List<Product> products = new ArrayList<>();
        try {
            String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);
            String searchUrl = URL_BASE + encodedProductName + "#D[A:" + encodedProductName + "]";
            Document doc = Jsoup.connect(searchUrl).get();
            Elements productElements = doc.select("li.ui-search-layout__item");

            List<String> queryWords = Arrays.asList(productName.toLowerCase().split(" "));
            for (Element productElement : productElements) {
                // Extraer nombre del producto, precio y enlace
                String name = productElement.select("h2.poly-box a").text().toLowerCase();
                String link = productElement.select("h2.poly-box a").attr("href");
                String price = Objects.requireNonNull(productElement.select("span.andes-money-amount__fraction").first()).text();
                String logo = "https://http2.mlstatic.com/static/org-img/homesnw/mercado-libre.png?v=2";

                double priceDouble = ConvertPrice.convertPriceDouble(price);
                boolean allWordsMatch = queryWords.stream().allMatch(name::contains);

                if (allWordsMatch && !name.isEmpty() && !price.isEmpty() && !link.isEmpty()) {
                    products.add(new Product(name, priceDouble, link, logo));
                }
            }

        } catch (IOException e) {
            throw new ScraperException("Error connecting to Mercado Libre", e);
        }
        return products;
    }
}
