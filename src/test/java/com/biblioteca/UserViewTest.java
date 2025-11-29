package com.biblioteca;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.javalin.Javalin;
import com.biblioteca.controller.UserController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class UserViewTest {
    private static WebDriver driver;
    private static Javalin app;
    @BeforeAll static void setup()
    { WebDriverManager.chromedriver().setup();
        app = Javalin.create();
        new UserController(app);
        app.start(7000);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }
    @AfterAll static void teardown() {
        driver.quit();
        app.stop();
    }
    @Test void testHelloAdmin() {
        driver.get("http://localhost:7000/hello?user=alice@admin.com");
        String response = driver.findElement(By.tagName("body")).getText();
        Assertions.assertEquals("Bem vindo admin alice@admin.com! Chave:123456",response);
    }
    @Test void testHelloUser() {
        driver.get("http://localhost:7000/hello?user=bob@user.com");
        String response = driver.findElement(By.tagName("body")).getText();
        Assertions.assertEquals( "Bem vindo bob@user.com!",response);
    }
    @Test void testGetUser() {
       driver.get("http://localhost:7000/getUser?id=1");
       String response = driver.findElement(By.tagName("body")).getText();
       Assertions.assertEquals("alice@admin.com",response );
    }
}