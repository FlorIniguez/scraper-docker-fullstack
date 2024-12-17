package com.crawler.buscador.controller;

import com.crawler.buscador.Exceptions.ProductNotFoundException;
import com.crawler.buscador.service.ScraperService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/products")
@Slf4j
public class CrawlerController {
    @Autowired
    ScraperService scraperService;

   @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String query) {
        //Entrada de la solicitud
        log.info("Received request to search products with query: {}",query);
        try {
            // Llamar al servicio y capturar la respuesta
            Map<String, Object> response = scraperService.searchAndDisplay(query);
            log.info("Search completed successfully for query: {}", query);
            return ResponseEntity.ok(response);

        } catch (ProductNotFoundException e) {
            //Advertencia si no se encuentran productos
            log.error("Unexpected error while searching products for query: {}", query);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
       
        } catch (Exception e) {
            //Manejo de excepciones genericas
            log.error("Unexpected error while searching products for query: {}", query);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Collections.singletonMap("error", "Error al buscar productos"));
        }
    }  
}
