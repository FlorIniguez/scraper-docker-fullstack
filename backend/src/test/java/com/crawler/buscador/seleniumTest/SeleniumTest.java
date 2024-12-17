package com.crawler.buscador.seleniumTest;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest
public class SeleniumTest {
    // creo instancia del WebDriver
    private WebDriver driver;

    // BeforeEach para que este metodo se ejecute primero en cada prueba
    @BeforeEach
    public void setUp() {
        // Configuración automática de WebDriver
        WebDriverManager.chromedriver().setup();
        // crea unstancia de Chrome
        driver = new ChromeDriver();
        driver.get("http://localhost:3000");
    }

    // AfterEach para que se ejecute despues de cada prueba
    @AfterEach
    public void tearDown() {
        // PAra limpiar recursos usados en la prueba, cerrar navegador
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void seleniumTest() {
        // WebDriverWait es un clase de Selenium que se usa para esperas
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            // Esperar hasta que el campo de búsqueda sea visible
            WebElement searchField = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id(":r0:")));
            // Ingresar término de búsqueda
            searchField.sendKeys("teclado redragon");

            // Buscar el botón de búsqueda y hacer click
            WebElement searchButton = driver.findElement(By.className("MuiButtonBase-root"));
            searchButton.click();
            // Pausar para que la acción sea visible
            Thread.sleep(8000);

            // Esperar hasta que los resultados estén visibles
            List<WebElement> results = wait
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("MuiPaper-root")));

            // Verificar que haya al menos un resultado
            Assertions.assertFalse(results.isEmpty(), "No se encontraron resultados");

            // Mostrar los resultados
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (WebElement result : results) {
                js.executeScript("arguments[0].scrollIntoView(true);", result); // Scroll
                System.out.println(result.getText());
                Thread.sleep(500);
            }

            // Extraer todos los enlaces disponibles en los resultados
            List<WebElement> links = driver.findElements(By.tagName("a"));

            // Seleccionar un enlace aleatorio para probar redireccionamiento
            if (!links.isEmpty()) {
                WebElement randomLink = links.get(new Random().nextInt(links.size()));

                // Esperar a que el enlace sea clickeable
                WebDriverWait waitForClick = new WebDriverWait(driver, Duration.ofSeconds(10));
                waitForClick.until(ExpectedConditions.elementToBeClickable(randomLink));

                // Pausar para que el clic sea visible
                Thread.sleep(3000);

                // Espero que la pagina cargue y verifico el redireccionamiento
                wait.until(ExpectedConditions.urlContains("http"));
                String currentUrl = driver.getCurrentUrl();
                // Pausar para que la acción sea visible
                Thread.sleep(8000);
                System.out.println("Url actual: " + currentUrl);
            } else {
                System.out.println("No hay enlaces disponibles para probar");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error en la prueba: " + e.getMessage());
        }
    }
}
