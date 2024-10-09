package com.crawler.buscador.controller;

import com.crawler.buscador.Exceptions.ProductNotFoundException;
import com.crawler.buscador.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class CrawlerController {
    @Autowired
    ScraperService scraperService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String query) {
        Map<String, Object> response = scraperService.searchAndDisplay(query);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
