package com.crawler.buscador.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productName) {
        super(String.format("No se encontraron productos para: '%s'", productName));
    }
}

