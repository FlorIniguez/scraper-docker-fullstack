package com.crawler.buscador.seleniumTest;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest
public class SeleniumTest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        // Configuración automática de WebDriver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("http://localhost:3000"); 
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void seleniumTest() {
        // Crear instancia de WebDriverWait
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); 

        try {
            // Esperar hasta que el campo de búsqueda sea visible
            WebElement searchField = wait
                    .until(ExpectedConditions.elementToBeClickable(By.id(":r0:")));
            // Pausar para ver la interacción
            Thread.sleep(1000); 
            // Buscar el botón de búsqueda
            WebElement searchButton = driver.findElement(By.className("MuiButtonBase-root"));

            // Ingresar término de búsqueda
            searchField.sendKeys("teclado inalambrico");
            // Pausar para ver la interacción
            Thread.sleep(1000); 

            // Hacer clic en el botón de búsqueda
            searchButton.click(); 
            // Pausar para ver la interacción
            Thread.sleep(10000); 

            // Esperar hasta que los resultados estén visibles
            List<WebElement> results = wait
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("MuiPaper-root")));

            // Imprimir resultados
            for (WebElement result : results) {
                System.out.println(result.getText());
            }

            // Validar que haya al menos un resultado
            Assertions.assertFalse(results.isEmpty(), "No se encontraron resultados");

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error en la prueba: " + e.getMessage());
        }
    }
}
