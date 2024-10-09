package com.crawler.buscador.service;

import com.crawler.buscador.Exceptions.ProductNotFoundException;
import com.crawler.buscador.crawler.GarbarinoScraper;
import com.crawler.buscador.crawler.MlibreScraper;
import com.crawler.buscador.crawler.RodoScraper;
import com.crawler.buscador.models.Product;
import com.crawler.buscador.utils.PriceComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.crawler.buscador.utils.PriceComparator.priceComparator;

@Service
public class ScraperService {

    @Autowired
    RodoScraper rodoScraper;
    @Autowired
    MlibreScraper mlibreScraper;
    @Autowired
    GarbarinoScraper garbarinoScraper;
    @Autowired
    PriceComparator priceComparator;

    public Map<String, Object> searchAndDisplay(String productName) {
        // Crear los CompletableFutures para cada scraper
        CompletableFuture<List<Product>> garbarinoFuture = CompletableFuture.supplyAsync(() -> garbarinoScraper.searchProduct(productName));
        CompletableFuture<List<Product>> mlibreFuture = CompletableFuture.supplyAsync(() -> mlibreScraper.searchProduct(productName));
        CompletableFuture<List<Product>> rodoFuture = CompletableFuture.supplyAsync(() -> rodoScraper.searchProduct(productName));

        // Unir todos los futures en uno solo que espera a que todos terminen
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(garbarinoFuture, mlibreFuture, rodoFuture);

        // Esperar que todos los futures se completen y luego obtener los resultados
        try {
            // Este bloque espera que todos los futures terminen
            allFutures.join();

            // Obtener los resultados de cada Future
            List<Product> garbarinoProducts = garbarinoFuture.get();
            List<Product> mlibreProducts = mlibreFuture.get();
            List<Product> rodoProducts = rodoFuture.get();

            // Si todos están vacíos, lanza la excepción
            if (garbarinoProducts.isEmpty() && mlibreProducts.isEmpty() && rodoProducts.isEmpty()) {
                throw new ProductNotFoundException(productName);
            }

            // Encontrar el producto más barato
            Optional<Product> cheapestProduct = priceComparator(garbarinoProducts, mlibreProducts, rodoProducts);

            // Construir la respuesta con el producto más barato y la lista ordenada
            Map<String, Object> response = new HashMap<>();
            cheapestProduct.ifPresent(product -> response.put("CheapestProduct", product));
            response.put("productList", Stream.of(rodoProducts, mlibreProducts, garbarinoProducts)
                    .flatMap(List::stream)
                    .sorted(Comparator.comparing(Product::getPrice))
                    .collect(Collectors.toList()));

            return response;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error occurred while scraping products", e);
        }
    }
}


