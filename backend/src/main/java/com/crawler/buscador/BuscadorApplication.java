package com.crawler.buscador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class BuscadorApplication {

    public static void main(String[] args) {
        log.info("Iniciando aplicacion");
        SpringApplication.run(BuscadorApplication.class, args);
    }
}