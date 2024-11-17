package com.crawler.buscador.service;

import com.crawler.buscador.Exceptions.ProductNotFoundException;
import com.crawler.buscador.crawler.GarbarinoScraper;
import com.crawler.buscador.crawler.MlibreScraper;
import com.crawler.buscador.crawler.RodoScraper;
import com.crawler.buscador.models.Product;
import com.crawler.buscador.utils.PriceComparator;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.crawler.buscador.utils.PriceComparator.priceComparator;

@Service
@Slf4j
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
        log.info("Starting product search for: {}", productName);
        // Crear los CompletableFutures para cada scraper, ejecuta cada scraper en un
        // hilo separado para buscar productos asincrónicamente.
        //System.currentTimeMillis() para calcular el tirmpo de ejecucion
        long overallStart = System.currentTimeMillis();
        CompletableFuture<List<Product>> garbarinoFuture = CompletableFuture
                .supplyAsync(() -> garbarinoScraper.searchProduct(productName))
                .exceptionally(ex -> {
                    log.error("Error in Garbarino scraper: {}", ex.getMessage());
                    return Collections.emptyList();
                });
        CompletableFuture<List<Product>> mlibreFuture = CompletableFuture
                .supplyAsync(() -> mlibreScraper.searchProduct(productName))
                .exceptionally(ex -> {
                    log.error("Error in Mercado libre scraper: {}", ex.getMessage());
                    return Collections.emptyList();
                });
        CompletableFuture<List<Product>> rodoFuture = CompletableFuture
                .supplyAsync(() -> rodoScraper.searchProduct(productName))
                .exceptionally(ex -> {
                    log.error("Error in Rodo scraper: {}", ex.getMessage());
                    return Collections.emptyList();
                });

        // Combina los CompletableFutures en uno solo con allOf()
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
                log.error("No product found");
                throw new ProductNotFoundException(productName);
            }

            // Encontrar el producto más barato
            log.info("Searching cheapest product for: {}", productName);
            Optional<Product> cheapestProduct = priceComparator(garbarinoProducts, mlibreProducts, rodoProducts);

            // Respuesta con el producto más barato y la lista ord1enada
            log.info("Creating response with sorted product list for: {}", productName);
            Map<String, Object> response = new HashMap<>();
            cheapestProduct.ifPresent(product -> response.put("CheapestProduct", product));
            log.debug("Sorting and combining products from all sources for: {}",productName);
            response.put("SortedProducts", Stream.of(rodoProducts, mlibreProducts, garbarinoProducts)
                    .flatMap(List::stream)
                    .sorted(Comparator.comparing(Product::getPrice))
                    .collect(Collectors.toList()));

            // Cuanto tardo
            long overallEnd = System.currentTimeMillis();
            log.info("Total execution time: {} ms", overallEnd - overallStart);

            return response;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while fetching products for {}: {}", productName, e.getMessage(), e);
            throw new RuntimeException("Error occurred while scraping products", e);
        }
    }
}
