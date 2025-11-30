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
import org.openqa.selenium.support.ui.Select;

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

        WebElement categorySelect = driver.findElement(By.name("category"));
        new Select(categorySelect).selectByVisibleText("Romance");

        driver.findElement(By.cssSelector("form")).submit();

        Assertions.assertTrue(driver.getCurrentUrl().endsWith("/biblioteca"));
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("Dom Casmurgo"));
        Assertions.assertTrue(pageSource.contains("Romance")); // opcional
    }

    @Test
    void testEditBook() {
        driver.get("http://localhost:7000/biblioteca");

        WebElement link = driver.findElement(By.linkText("Editar"));
        link.click();

        driver.findElement(By.name("name")).clear();
        driver.findElement(By.name("name")).sendKeys("editedBook");
        driver.findElement(By.name("autor")).clear();
        driver.findElement(By.name("autor")).sendKeys("editedBookAutor");

        WebElement categorySelect = driver.findElement(By.name("category"));
        new Select(categorySelect).selectByVisibleText("Fantasia");

        driver.findElement(By.cssSelector("form")).submit();

        Assertions.assertTrue(driver.getCurrentUrl().endsWith("/biblioteca"));
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("editedBook"));
    }

    @Test
    void testDeleteBiblioteca() {
        driver.get("http://localhost:7000/biblioteca");

        WebElement deleteButton = driver.findElement(
                By.xpath("//tr[td[contains(text(), 'Frankenstein')]]//button[contains(text(), 'Deletar') or contains(., 'Deletar')]")
        );
        deleteButton.click();

        Assertions.assertFalse(driver.getPageSource().contains("Frankenstein"));
    }

    @Test
    void testCreateBookWithAllCategories() {
        String[] categories = {"Ficção", "Não-Ficção", "Romance", "Mistério",
                "Fantasia", "Terror", "Ciência Ficção", "Biografia",
                "Poesia", "Distopia", "Infantil", "Épico"};

        for (String category : categories) {
            driver.get("http://localhost:7000/biblioteca");

            WebElement link = driver.findElement(By.linkText("Adicionar Novo Livro"));
            link.click();

            driver.findElement(By.name("name")).sendKeys("Test " + category);
            driver.findElement(By.name("autor")).sendKeys("Author " + category);

            WebElement categorySelect = driver.findElement(By.name("category"));
            new Select(categorySelect).selectByVisibleText(category);

            driver.findElement(By.cssSelector("form")).submit();

            Assertions.assertTrue(driver.getCurrentUrl().endsWith("/biblioteca"));
            String pageSource = driver.getPageSource();
            Assertions.assertTrue(pageSource.contains(category));
        }
    }

    @Test
    void testEditBookChangeAllFields() {
        driver.get("http://localhost:7000/biblioteca");

        WebElement link = driver.findElement(By.linkText("Editar"));
        link.click();

        WebElement nameField = driver.findElement(By.name("name"));
        nameField.clear();
        nameField.sendKeys("Name Updated");

        WebElement autorField = driver.findElement(By.name("autor"));
        autorField.clear();
        autorField.sendKeys("Autor Updated");

        WebElement categorySelect = driver.findElement(By.name("category"));
        new Select(categorySelect).selectByVisibleText("Terror");

        driver.findElement(By.cssSelector("form")).submit();

        Assertions.assertTrue(driver.getCurrentUrl().endsWith("/biblioteca"));
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("Name Updated"));
        Assertions.assertTrue(pageSource.contains("Terror"));
    }

    @Test
    void testTableDisplaysAllBooks() {
        driver.get("http://localhost:7000/biblioteca");

        WebElement table = driver.findElement(By.tagName("table"));
        Assertions.assertNotNull(table);

        WebElement thead = driver.findElement(By.tagName("thead"));
        String headerText = thead.getText();
        Assertions.assertTrue(headerText.contains("ID"));
        Assertions.assertTrue(headerText.contains("Nome do Livro"));
        Assertions.assertTrue(headerText.contains("Autor"));
        Assertions.assertTrue(headerText.contains("Gênero"));
        Assertions.assertTrue(headerText.contains("Ações"));
    }

    @Test
    void testFormValidationOnNewBook() {
        driver.get("http://localhost:7000/biblioteca/new");

        WebElement form = driver.findElement(By.tagName("form"));
        Assertions.assertNotNull(form);

        WebElement nameInput = driver.findElement(By.name("name"));
        Assertions.assertTrue(nameInput.getAttribute("required") != null);

        WebElement autorInput = driver.findElement(By.name("autor"));
        Assertions.assertTrue(autorInput.getAttribute("required") != null);

        WebElement categorySelect = driver.findElement(By.name("category"));
        Assertions.assertTrue(categorySelect.getAttribute("required") != null);
    }

    @Test
    void testNavigationBetweenPages() {
        driver.get("http://localhost:7000/biblioteca");

        driver.findElement(By.linkText("Adicionar Novo Livro")).click();
        Assertions.assertTrue(driver.getCurrentUrl().contains("/new"));

        driver.navigate().back();
        Assertions.assertTrue(driver.getCurrentUrl().contains("/biblioteca"));
    }
}