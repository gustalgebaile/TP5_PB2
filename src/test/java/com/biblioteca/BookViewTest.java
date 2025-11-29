package com.biblioteca;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.javalin.Javalin;
import com.biblioteca.controller.BibliotecaController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BookViewTest {

    private static WebDriver driver;
    private static Javalin app;

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().setup();

        app = Javalin.create();
        new BibliotecaController(app);
        app.start(7000);


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }
    @AfterAll
    static void teardown() {
        driver.quit();
        app.stop();
    }

    @Test
    void testCreateBook() {
        driver.get("http://localhost:7000/biblioteca");

        WebElement link = driver.findElement(By.linkText("Adicionar Novo Livro"));
        link.click();

        driver.findElement(By.name("name")).sendKeys("Dom Casmurgo");
        driver.findElement(By.name("autor")).sendKeys("Machado de Assis");

        driver.findElement(By.cssSelector("form")).submit();

        Assertions.assertTrue(driver.getCurrentUrl().endsWith("/biblioteca"));

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("Dom Casmurgo"));
    }
    @Test
    void testEditBook() {
        driver.get("http://localhost:7000/biblioteca");

        WebElement link = driver.findElement(By.linkText("Editar"));
        link.click();

        driver.findElement(By.name("name")).sendKeys("editedBook");
        driver.findElement(By.name("autor")).sendKeys("editedBookAutor");

        driver.findElement(By.cssSelector("form")).submit();

        Assertions.assertTrue(driver.getCurrentUrl().endsWith("/biblioteca"));

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("editedBook"));
    }

    @Test
    void testDeleteBiblioteca() {
        driver.get("http://localhost:7000/biblioteca");

        WebElement deleteButton = driver.findElement(
                By.xpath("//tr[td[contains(text(), 'Alice no País das Maravilhas')]]//button[contains(text(), 'Deletar') or contains(., 'Deletar')]")
        );
        deleteButton.click();

        Assertions.assertFalse(driver.getPageSource().contains("Alice no País das Maravilhas"));
    }
}

