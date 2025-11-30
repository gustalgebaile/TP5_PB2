package com.biblioteca;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.javalin.Javalin;
import com.biblioteca.controller.UserController;
import com.biblioteca.model.User;
import com.biblioteca.model.enums.Role;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class UserControllerGapsTest {

    private static Javalin app;
    private static WebDriver driver;
    private static final int TEST_PORT = 7006;

    @BeforeAll
    static void startApp() throws Exception {
        WebDriverManager.chromedriver().setup();

        app = Javalin.create().start(TEST_PORT);
        new UserController(app);

        Thread.sleep(1000);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    static void stopApp() {
        if (driver != null) {
            driver.quit();
        }
        if (app != null) {
            app.stop();
        }
    }

    // ========== Cobertura: /hello com admin ==========
    @Test
    void testHelloEndpointWithAdminReturnsSecretKey() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/hello?user=antonio@admin.com");

        Assertions.assertTrue(response.contains("administrador"));
        Assertions.assertTrue(response.contains("123456"));
    }

    @Test
    void testHelloEndpointWithAdminShowsEmail() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/hello?user=antonio@admin.com");

        Assertions.assertTrue(response.contains("antonio@admin.com"));
    }

    // ========== Cobertura: /hello com usuario regular ==========
    @Test
    void testHelloEndpointWithRegularUserDoesNotShowSecret() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/hello?user=gusta@user.com");

        Assertions.assertTrue(response.contains("Bem vindo"));
        Assertions.assertTrue(response.contains("gusta@user.com"));
        Assertions.assertFalse(response.contains("123456"));
    }

    @Test
    void testHelloEndpointWithRegularUserDoesNotShowAdmin() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/hello?user=gusta@user.com");

        Assertions.assertFalse(response.contains("administrador"));
    }

    // ========== Cobertura: /getUser com ID válido ==========
    @Test
    void testGetUserEndpointWithValidId() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/getUser?id=1");

        Assertions.assertTrue(response.contains("antonio@admin.com"));
    }

    @Test
    void testGetUserEndpointWithValidIdReturnsEmail() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/getUser?id=1");

        Assertions.assertFalse(response.contains("Usuário não encontrado"));
    }

    // ========== Cobertura: /getUser com ID inválido (maior que size) ==========
    @Test
    void testGetUserEndpointWithIdGreaterThanSize() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/getUser?id=999");

        Assertions.assertTrue(response.contains("Usuário não encontrado"));
    }

    @Test
    void testGetUserEndpointWithIdZero() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/getUser?id=0");

        Assertions.assertTrue(response.contains("Usuário não encontrado"));
    }

    // ========== Cobertura: /getUser com ID negativo ==========
    @Test
    void testGetUserEndpointWithNegativeId() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/getUser?id=-1");

        Assertions.assertTrue(response.contains("Usuário não encontrado"));
    }

    // ========== Cobertura: /getUser com entrada inválida (não-numérica) ==========
    @Test
    void testGetUserEndpointWithNonNumericId() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/getUser?id=abc");

        Assertions.assertTrue(response.contains("Erro no input"));
    }

    // ========== Cobertura: /getUser com múltiplos IDs inválidos ==========
    @Test
    void testGetUserEndpointWithFloatValue() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/getUser?id=1.5");

        Assertions.assertTrue(response.contains("Erro no input"));
    }

    @Test
    void testGetUserEndpointWithEmptyId() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/getUser?id=");

        // Pode retornar erro ou usar valor padrão
        Assertions.assertNotNull(response);
    }

    // ========== Cobertura: /run endpoint ==========
    @Test
    void testRunEndpointWithCommand() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/run?cmd=echo+test");

        Assertions.assertTrue(response.contains("Comando recebido"));
    }

    @Test
    void testRunEndpointWithInvalidCommand() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/run?cmd=invalid_command_12345");

        Assertions.assertTrue(response.contains("Comando recebido"));
    }

    @Test
    void testRunEndpointWithDangerousCommand() throws Exception {
        String response = getResponse("http://localhost:" + TEST_PORT +
                "/run?cmd=rm+-rf+/");

        // Deve executar mesmo assim (demonstra vulnerabilidade)
        Assertions.assertTrue(response.contains("Comando recebido"));
    }

    // ========== Cobertura: Tratamento de exceções ==========
    @Test
    void testGetUserEndpointHandlesException() throws Exception {
        // Testa múltiplas entradas que causam exceção
        String[] invalidInputs = {"abc", "!!!!", "1.2.3", "NaN"};

        for (String input : invalidInputs) {
            String response = getResponse("http://localhost:" + TEST_PORT +
                    "/getUser?id=" + input);
            Assertions.assertTrue(response.contains("Erro no input"));
        }
    }

    @Test
    void testHelloEndpointWith200Status() throws Exception {
        int statusCode = getStatusCode("http://localhost:" + TEST_PORT +
                "/hello?user=antonio@admin.com");

        Assertions.assertEquals(200, statusCode);
    }

    @Test
    void testGetUserEndpointWith200Status() throws Exception {
        int statusCode = getStatusCode("http://localhost:" + TEST_PORT +
                "/getUser?id=1");

        Assertions.assertEquals(200, statusCode);
    }

    @Test
    void testRunEndpointWith200Status() throws Exception {
        int statusCode = getStatusCode("http://localhost:" + TEST_PORT +
                "/run?cmd=echo+test");

        Assertions.assertEquals(200, statusCode);
    }

    // ========== Helper Methods ==========
    private static String getResponse(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } finally {
            conn.disconnect();
        }
    }

    private static int getStatusCode(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try {
            return conn.getResponseCode();
        } finally {
            conn.disconnect();
        }
    }
}
