package com.crawler.buscador.scrapers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.crawler.buscador.Exceptions.ProductNotFoundException;
import com.crawler.buscador.crawler.GarbarinoScraper;
import com.crawler.buscador.models.Product;

public class GarbarinoScraperTest {

    private static GarbarinoScraper garbarinoScraper;

    @BeforeEach
    public void setUp() {
        garbarinoScraper = new GarbarinoScraper();
    }

    @Test
    public void testGetBaseUrl() {
        // llamo al metodo de esa clase
        String baseUrl = garbarinoScraper.getBaseUrl();
        // Assert. Verifico si la url que obtengo es la esperada
        assertEquals("https://www.garbarino.com/shop?search=", baseUrl,
                "La URL base no coincide con la esperada.");
    }

    @Test
    // Construccion de la URL con el nombre del producto buscado
    public void testBuildUrl() {
        // los 2 datos que usa el metodo
        String baseUrl = "https://www.garbarino.com/shop?search=";
        String productName = "mouse optico";

        // llamo al metodo
        String actualUrl = garbarinoScraper.buildSearchUrl(baseUrl, productName);

        // Resultado esperado, productName se codifica para usar en la URL
        String expectedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);
        String expectedUrl = baseUrl + expectedProductName;
        assertEquals(expectedUrl, actualUrl,
                "La URL generada para la busqeuda no coincide con la esperada.");
    }

    @Test
    // Test obtencion de datos de la pagina oficial
    public void testParseProduct() {
        // HTML simulado con el fragmento de producto
        String html = "<div data-v-f2d40992='' class='product-card-design6-vertical__data-cont d-flex flex-column mx-3'>"
                +
                "<div data-v-0b6e3ae8='' data-v-f2d40992='' class='product-card-design6-vertical__price-container'>" +
                "<div data-v-0b6e3ae8=''>" +
                "<div data-v-4e0e78d1=''>" +
                "<div data-v-592c7530='' data-v-4e0e78d1='' class='text-no-wrap product-card-design6-vertical__price price font-6 line-clamp-1 mt-2 px-1 text-center'>"
                +
                "<span data-v-592c7530=''></span><span data-v-592c7530='' class='mr-1'>$</span><span data-v-592c7530=''>2.399</span>"
                +
                "</div></div></div></div>" +
                "<a data-v-f2d40992='' href='/p/mouse-optico-kanji-kj-mouset001-800-dpi-luz-led-black/689a0f1f-2932-4648-8f13-86a63122b8e5' class='card-anchor header'>"
                +
                "<div data-v-f2d40992=''>" +
                "<div data-v-f2d40992='' class='product-card-design6-vertical__name line-clamp-2 font-2 px-1 header text-center'>"
                +
                "MOUSE OPTICO KANJI KJ-MOUSET001 800 DPI LUZ LED BLACK" +
                "</div></div></a>" +
                "<div data-v-f2d40992=''>" +
                "<div data-v-f2d40992='' class='product-card-design6-vertical__brand-wrapper'>" +
                "<div data-v-f2d40992='' class='product-card-design6-vertical__brand font-0 line-clamp-1 px-1 text-center mb-2'>"
                +
                "Kanji" +
                "</div></div></div></div>";

        // Convertir el HTML a un documento Jsoup
        Document doc = Jsoup.parse(html);

        // Extraer NOMBRE del producto
        Element nameElement = doc.select("div.product-card-design6-vertical__name").first();
        String productName = nameElement != null ? nameElement.text() : "";

        // Extraer PRECIO
        Element priceElement = doc.select("div.product-card-design6-vertical__price span:last-child").first();
        // Si el precio no es NULO lo convierte a texto
        String productPrice = priceElement != null ? priceElement.text() : "";

        // extraer el ENLACE del producto
        Element linkElement = doc.select("a.card-anchor.header").first();
        String productLink = linkElement != null ? linkElement.attr("href") : "";

        assertEquals("MOUSE OPTICO KANJI KJ-MOUSET001 800 DPI LUZ LED BLACK", productName);
        assertEquals("2.399", productPrice);
        assertEquals("/p/mouse-optico-kanji-kj-mouset001-800-dpi-luz-led-black/689a0f1f-2932-4648-8f13-86a63122b8e5",
                productLink);
    }

    @Test
    //Si encuentra un producto, que lo sume a la lista
    public void testParseProducts_FoundProducts() {

        String html = "<div data-v-f2d40992='' data-v-557a2003='' class='h100 product-card-design6-vertical-wrapper' elementorder='[object Object]' product='[object Object]' list-id='/shop?search=teclado inalambrico' list-index='2' verticalcard='true'>"
                +
                "<div data-v-f2d40992='' tabindex='0' class='v-card v-card--link v-sheet theme--light elevation-2 product-card-design6-vertical d-flex flex-column base rounded-md overflow-hidden product-card pb-2' style='height: 100%;'>"
                +
                "<div data-v-f2d40992='' class='product-card-design6-vertical__image-cont no-stock'>" +
                "<div data-v-f2d40992=''>" +
                "<div data-v-383a1ef0='' class='product-card-design6-vertical'>" +
                "<div data-v-536d58b2='' data-v-6a796a72='' data-v-383a1ef0='' class='ratio-box ratio-image product-card-design6-vertical__main-image' style='width: 100%;'>"
                +
                "<div data-v-536d58b2='' class='ratio-box__sizer' style='padding-bottom: 100%;'></div>" +
                "<div data-v-536d58b2='' class='ratio-box__slot'>" +
                "<img data-v-6a796a72='' data-v-536d58b2='' alt='Teclado Mecanico Inalambrico Logitech Pop Keys Bluetooth Usb Rosa' src='https://d2eebw31vcx88p.cloudfront.net/garbarino/uploads/0aa15535a876ecb435080ff4aa6aace18a39096d.jpg.webp' class='ratio-image__image' style='object-fit: contain; object-position: center center;'>"
                +
                "<div data-v-6a796a72='' data-v-536d58b2='' class='ratio-image__content'></div>" +
                "</div></div></div></div></div>" +
                "<div data-v-f2d40992=''>" +
                "<div data-v-54ffb860='' class='container pa-0 pt-1 product-card-tags-vertical product-card-tags-vertical-design6'>"
                +
                "<div data-v-54ffb860='' class='product-card-tags-vertical__attrs-wrapper'></div>" +
                "<div data-v-54ffb860='' class='product-card-tags-vertical__div-fixed-height'></div>" +
                "</div></div>" +
                "<div data-v-f2d40992='' class='product-card-design6-vertical__data-cont d-flex flex-column mx-3 no-stock'>"
                +
                "<div data-v-0b6e3ae8='' data-v-f2d40992='' class='product-card-design6-vertical__price-container'>" +
                "<div data-v-0b6e3ae8=''>" +
                "<div data-v-0b6e3ae8=''>" +
                "<div data-v-4e0e78d1=''>" +
                "<div data-v-592c7530='' data-v-4e0e78d1='' class='text-no-wrap product-card-design6-vertical__price price font-6 line-clamp-1 mt-2 px-1 text-center'>"
                +
                "<span data-v-592c7530=''></span><span data-v-592c7530='' class='mr-1'>$</span><span data-v-592c7530=''>149.999</span>"
                +
                "</div></div></div>" +
                "<div data-v-f2d40992=''>" +
                "<a data-v-f2d40992='' href='/p/teclado-mecanico-inalambrico-logitech-pop-keys-bluetooth-usb-rosa/02ff8282-0d7b-47d5-b2b5-37d756fffbbf' class='card-anchor header'>"
                +
                "<div data-v-f2d40992=''>" +
                "<div data-v-f2d40992='' class='product-card-design6-vertical__name line-clamp-2 font-2 px-1 header text-center'> Teclado Mecanico Inalambrico Logitech Pop Keys Bluetooth Usb Rosa </div>"
                +
                "</div></a>" +
                "<div data-v-f2d40992=''>" +
                "<div data-v-f2d40992='' class='product-card-design6-vertical__brand-wrapper'>" +
                "<div data-v-f2d40992='' class='product-card-design6-vertical__brand font-0 line-clamp-1 px-1 text-center mb-2'> Logitech </div>"
                +
                "</div></div>" +
                "</div></div></div>";
        // Convierto HTML
        Document doc = Jsoup.parse(html);
        // Llamo al metodo parseProducts
        String productName = "teclado mecanico";
        List<Product> products = garbarinoScraper.parseProducts(doc, productName);

        // VERIFICAR SI ENCONTRO EL PRODUCTO
        assertEquals(1, products.size(), "Deberia encontrar un producto");
        assertTrue(products.get(0).getName().toLowerCase().contains(productName.toLowerCase()),
                "El nombre del producto no contiene el texto esperado");
        assertEquals(149999.00, products.get(0).getPrice(), "El precio del producto no es el correcto");
    }

    @Test
    // Test en caso de no encontrar productos, verificar excepcion
    public void testSearchProductNotResult() {
        String productName = "ProductoInexistente";
        // assertThrows captura la excepcion y valida su tipo y contenido
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            garbarinoScraper.searchProduct(productName);
        });
        // Verificar el mensaje de la excepción
        String expectedMessage = String.format("No se encontraron productos para: '%s'", productName);
        assertEquals(expectedMessage, exception.getMessage(),
                "El mensaje de la excepción no coincide con el esperado.");
    }
}