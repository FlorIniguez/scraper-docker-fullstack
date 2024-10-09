package com.crawler.buscador.utils;

import com.crawler.buscador.models.Product;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class PriceComparator {
    public static Optional<Product> priceComparator(List<Product> listStore1, List<Product> listStore2, List<Product> listStore3) {
        // Combinar los productos
        List<Product> allProducts = Stream.of(listStore1, listStore2, listStore3)
                .flatMap(List::stream)
                .toList();
        // Encontrar el producto más barato
        return allProducts.stream()
                .filter(product -> product.getPrice() > 0)
                .min(Comparator.comparingDouble(Product::getPrice));
    }
}


