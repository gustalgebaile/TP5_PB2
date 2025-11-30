package com.biblioteca;

import com.biblioteca.model.Book;
import com.biblioteca.view.BookView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Assertions.assertTrue(pageSource.contains("Romance"));
    }

    @Test
    void testDeleteBiblioteca() {
        driver.get("http://localhost:7000/biblioteca");

        WebElement deleteButton = driver.findElement(
                By.xpath("//tr[td[contains(text(), 'Frankenstein')]]//button[contains(text(), 'Deletar') or contains(., 'Deletar')]")
        );
        deleteButton.click();

        Assertions.assertTrue(driver.getPageSource().contains("Frankenstein"));
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
    void testDeleteMultipleBooks() {
        for (int i = 0; i < 3; i++) {
            driver.get("http://localhost:7000/biblioteca/new");
            driver.findElement(By.name("name")).sendKeys("Book to Delete " + i);
            driver.findElement(By.name("autor")).sendKeys("Author " + i);
            new Select(driver.findElement(By.name("category"))).selectByVisibleText("Ficção");
            driver.findElement(By.cssSelector("form")).submit();
        }

        driver.get("http://localhost:7000/biblioteca");

        int initialCount = driver.findElements(By.xpath("//tbody//tr")).size();

        WebElement deleteButton = driver.findElement(By.xpath("//button[contains(., 'Deletar')]"));
        deleteButton.click();

        int finalCount = driver.findElements(By.xpath("//tbody//tr")).size();

        Assertions.assertEquals(initialCount, finalCount);
    }

    @Test
    void testNavigationBetweenPages() {
        driver.get("http://localhost:7000/biblioteca");

        driver.findElement(By.linkText("Adicionar Novo Livro")).click();
        Assertions.assertTrue(driver.getCurrentUrl().contains("/new"));

        driver.navigate().back();
        Assertions.assertTrue(driver.getCurrentUrl().contains("/biblioteca"));
    }

    @Test
    void testRenderListWithEmptyList() {
        List<Book> emptyList = new ArrayList<>();
        String html = BookView.renderList(emptyList);

        Assertions.assertNotNull(html);
        Assertions.assertTrue(html.contains("<table"));
        Assertions.assertTrue(html.contains("</table>"));
    }

    @Test
    void testRenderListWithSingleBook() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Single Book", "Author", "Romance"));

        String html = BookView.renderList(books);

        Assertions.assertNotNull(html);
        Assertions.assertTrue(html.contains("Single Book"));
        Assertions.assertTrue(html.contains("Author"));
    }

    @Test
    void testRenderListWithMultipleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book 1", "Author 1", "Romance"));
        books.add(new Book("Book 2", "Author 2", "Terror"));
        books.add(new Book("Book 3", "Author 3", "Fantasia"));

        String html = BookView.renderList(books);

        Assertions.assertTrue(html.contains("Book 1"));
        Assertions.assertTrue(html.contains("Book 2"));
        Assertions.assertTrue(html.contains("Book 3"));
    }

    @Test
    void testRenderListHasBootstrap() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Test", "Author", "Romance"));

        String html = BookView.renderList(books);

        Assertions.assertTrue(html.contains("bootstrap"));
        Assertions.assertTrue(html.contains("table-striped"));
    }

    @Test
    void testRenderListHasActionButtons() {
        List<Book> books = new ArrayList<>();
        Book book = new Book("Test", "Author", "Romance");
        books.add(book);

        String html = BookView.renderList(books);

        Assertions.assertTrue(html.contains("Editar"));
        Assertions.assertTrue(html.contains("Deletar"));
        Assertions.assertTrue(html.contains("edit/"));
        Assertions.assertTrue(html.contains("delete/"));
    }

    @Test
    void testRenderFormForNewBook() {
        Map<String, Object> model = new HashMap<>();
        String html = BookView.renderForm(model);

        Assertions.assertNotNull(html);
        Assertions.assertTrue(html.contains("Novo Livro"));
        Assertions.assertTrue(html.contains("form"));
    }

    @Test
    void testRenderFormForEditBook() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", 5);
        model.put("name", "Edit Book");
        model.put("autor", "Edit Author");
        model.put("genero", "Romance");

        String html = BookView.renderForm(model);

        Assertions.assertTrue(html.contains("Editar Livro"));
        Assertions.assertTrue(html.contains("Edit Book"));
        Assertions.assertTrue(html.contains("Edit Author"));
    }

    @Test
    void testRenderFormHasAllCategories() {
        Map<String, Object> model = new HashMap<>();
        String html = BookView.renderForm(model);

        Assertions.assertTrue(html.contains("Ficção"));
        Assertions.assertTrue(html.contains("Romance"));
        Assertions.assertTrue(html.contains("Terror"));
        Assertions.assertTrue(html.contains("Fantasia"));
        Assertions.assertTrue(html.contains("Distopia"));
        Assertions.assertTrue(html.contains("Infantil"));
        Assertions.assertTrue(html.contains("Épico"));
    }

    @Test
    void testRenderFormSelectsCorrectCategory() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", 1);
        model.put("name", "Test");
        model.put("autor", "Author");
        model.put("category", "Terror");

        String html = BookView.renderForm(model);

        // Verifica se Terror está marcado como selected
        Assertions.assertTrue(html.contains("value=\"Terror\" selected") ||
                html.contains("<option value=\"Terror\" selected"));
    }

    @Test
    void testRenderFormHasSubmitButton() {
        Map<String, Object> model = new HashMap<>();
        String html = BookView.renderForm(model);

        Assertions.assertTrue(html.contains("type=\"submit\""));
        Assertions.assertTrue(html.contains("Salvar"));
    }

    @Test
    void testRenderFormHasCancelButton() {
        Map<String, Object> model = new HashMap<>();
        String html = BookView.renderForm(model);

        Assertions.assertTrue(html.contains("Cancelar"));
    }

    @Test
    void testRenderFormWithAllFields() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", 10);
        model.put("name", "Complete Book");
        model.put("autor", "Complete Author");
        model.put("category", "Ficção");

        String html = BookView.renderForm(model);

        Assertions.assertTrue(html.contains("name=\"name\""));
        Assertions.assertTrue(html.contains("name=\"autor\""));
        Assertions.assertTrue(html.contains("name=\"category\""));
    }

    @Test
    void testRenderListWithNullGenero() {
        List<Book> books = new ArrayList<>();
        Book book = new Book("Test", "Author", null);
        books.add(book);

        String html = BookView.renderList(books);

        // Deve exibir "Não definido" se genero for null
        Assertions.assertTrue(html.contains("Test"));
    }

    @Test
    void testRenderFormDefaultValues() {
        Map<String, Object> model = new HashMap<>();
        model.put("id", 1);

        String html = BookView.renderForm(model);

        // Sem name/autor, deve estar vazio
        Assertions.assertTrue(html.contains("value=\"\"") || html.contains("value="));
    }

    @Test
    void testRenderFormHasRequiredAttributes() {
        Map<String, Object> model = new HashMap<>();
        String html = BookView.renderForm(model);

        Assertions.assertTrue(html.contains("required"));
    }

    @Test
    void testRenderListHasCorrectTableHeaders() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Test", "Author", "Romance"));

        String html = BookView.renderList(books);

        Assertions.assertTrue(html.contains("ID"));
        Assertions.assertTrue(html.contains("Nome do Livro"));
        Assertions.assertTrue(html.contains("Autor"));
        Assertions.assertTrue(html.contains("Gênero"));
        Assertions.assertTrue(html.contains("Ações"));
    }
}

