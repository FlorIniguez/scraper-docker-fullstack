package com.crawler.buscador.utils;

import org.springframework.stereotype.Component;

@Component
public class ConvertPrice {
    public static double convertPriceDouble(String price) {
        if (price == null || price.isEmpty()) {
            return 0.0;
        }
        String cleanPrice = price.replaceAll("[^\\d.,]", "").trim();

        // Comprobar si tiene un formato con comas como separador de miles
        if (cleanPrice.contains(",")) {
            // Si tiene un punto, se asume que es decimal
            if (cleanPrice.lastIndexOf(".") > cleanPrice.lastIndexOf(",")) {
                // Formato con punto como decimal: "342,999.00"
                cleanPrice = cleanPrice.replace(",", "").replace(".", ".");
            } else {
                // Formato con coma como separador de miles: "1.299.999"
                cleanPrice = cleanPrice.replace(".", "").replace(",", ".");
            }
        } else {
            // Solo números, sin separadores
            cleanPrice = cleanPrice.replace(".", "");
        }
        return Double.parseDouble(cleanPrice);
    }


}
