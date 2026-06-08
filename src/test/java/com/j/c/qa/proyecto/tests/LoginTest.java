package com.j.c.qa.proyecto.tests;

import com.j.c.qa.proyecto.pages.LoginPage;
import com.j.c.qa.proyecto.utils.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Pruebas de autenticación sobre la pantalla /login (Thymeleaf, no Angular).
 * Requiere que el servidor Spring Boot esté corriendo en localhost:8080.
 */
public class LoginTest extends BaseTest {

    private static final String BASE_URL          = "http://localhost:8080";
    private static final String USUARIO           = "juan";
    private static final String PASSWORD_ERRONEA  = "claveIncorrecta999";

    // Credenciales del admin inicial creado por AdminInitializer.java
    private static final String ADMIN_USUARIO     = "admin";
    private static final String ADMIN_PASSWORD    = "00000000";

    /**
     * Valida que, tras 3 intentos fallidos consecutivos con credenciales incorrectas,
     * el sistema muestre un mensaje de cuenta bloqueada.
     *
     * NOTA: si el backend no implementa bloqueo de cuenta, este test
     * fallará intencionalmente, evidenciando la feature faltante.
     */
    @Test
    public void validarBloqueoPorIntentosFallidos() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.abrir(BASE_URL);

        // 3 intentos fallidos consecutivos
        for (int intento = 1; intento <= 3; intento++) {
            loginPage.ingresarUsuario(USUARIO);
            loginPage.ingresarPassword(PASSWORD_ERRONEA);
            loginPage.clicIniciarSesion();
        }

        // Verificar que hay mensaje de error visible tras el tercer intento
        Assert.assertTrue(
            loginPage.errorEstaVisible(),
            "No se encontró ningún mensaje de error tras 3 intentos fallidos"
        );

        String mensajeActual = loginPage.obtenerMensajeError();

        // Verificar que el mensaje indica bloqueo de cuenta
        Assert.assertTrue(
            mensajeActual.toLowerCase().contains("bloqueada") ||
            mensajeActual.toLowerCase().contains("bloqueado"),
            "Mensaje esperado: texto sobre cuenta bloqueada. " +
            "Mensaje real obtenido: [" + mensajeActual + "]. " +
            "Posible causa: el backend no implementa bloqueo por intentos fallidos."
        );
    }

    /**
     * Caso positivo: verifica que el framework Selenium funciona end-to-end.
     * Credenciales extraídas de AdminInitializer.java (usuario semilla del sistema).
     * Post-login: CustomAuthenticationSuccessHandler redirige ADMIN a /admin/dashboard.
     * Validación doble: URL final + encabezado h1 del dashboard (Thymeleaf, no Angular).
     */
    @Test
    public void loginExitoso() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.abrir(BASE_URL);

        loginPage.iniciarSesion(ADMIN_USUARIO, ADMIN_PASSWORD);

        // Validación 1: la URL debe cambiar a /admin/dashboard tras el redirect del SuccessHandler
        String urlActual = driver.getCurrentUrl();
        Assert.assertTrue(
            urlActual.contains("/admin/dashboard"),
            "Se esperaba redirección a /admin/dashboard, pero la URL actual es: " + urlActual
        );

        // Validación 2: el dashboard muestra el encabezado de bienvenida del panel admin
        // Selector: <h1>Panel de Administración</h1> dentro de div.container (dashboard.html)
        String tituloDashboard = driver.findElement(By.cssSelector("div.container > h1")).getText();
        Assert.assertEquals(
            tituloDashboard,
            "Panel de Administración",
            "El título del dashboard no coincide con el esperado"
        );
    }
}
