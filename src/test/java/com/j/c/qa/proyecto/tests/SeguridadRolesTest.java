package com.j.c.qa.proyecto.tests;

import com.j.c.qa.proyecto.pages.LoginPage;
import com.j.c.qa.proyecto.utils.BaseTest;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Pruebas de control de acceso por rol (escalamiento de privilegios).
 * Requiere que el servidor Spring Boot esté corriendo en localhost:8080.
 */
public class SeguridadRolesTest extends BaseTest {

    private static final String BASE_URL          = "http://localhost:8080";
    private static final String OPERADOR_USUARIO  = "pedro";
    private static final String OPERADOR_PASSWORD = "12345678";

    /**
     * CP-SEG-02 (CP-08N) — Escalamiento de privilegios: usuario OPERADOR intenta
     * acceder directamente a una ruta de ADMIN vía URL.
     *
     * Precondición: usuario "pedro" existe con rol USUARIO (DataInitializer).
     *
     * Flujo:
     *   1. Login como operador (pedro / 12345678).
     *   2. Forzar navegación a /admin/rutas mediante driver.get().
     *   3. Validar que Spring Security bloqueó el acceso:
     *      - La URL final NO es /admin/rutas (redirect a /login o /error), O
     *      - El contenido de la página muestra un error 403 / "Access Denied".
     */
    @Test
    public void escalamientoDePrivilegios_CP08N() {
        // Paso 1 — autenticación como OPERADOR
        LoginPage loginPage = new LoginPage(driver);
        loginPage.abrir(BASE_URL);
        loginPage.iniciarSesion(OPERADOR_USUARIO, OPERADOR_PASSWORD);

        // El SuccessHandler redirige USUARIO → /usuario/venta
        wait.until(ExpectedConditions.urlContains("/usuario/venta"));

        // Paso 2 — intento de acceso forzado a ruta administrativa
        driver.get(BASE_URL + "/admin/rutas");

        // Paso 3 — verificar que Spring Security bloqueó el acceso
        String urlFinal = driver.getCurrentUrl();
        String contenidoPagina = driver.getPageSource().toLowerCase();

        // Caso A: Spring Security redirigió al login o a la página de error
        boolean redirigidoFueraDeAdmin = !urlFinal.contains("/admin/rutas");

        // Caso B: La respuesta HTTP fue 403 y el contenido lo indica
        // (Spring Boot Whitelabel Error Page incluye "403", "forbidden" o "access denied")
        boolean muestraError403 = contenidoPagina.contains("403")
                || contenidoPagina.contains("forbidden")
                || contenidoPagina.contains("access denied")
                || contenidoPagina.contains("whitelabel");

        Assert.assertTrue(
            redirigidoFueraDeAdmin || muestraError403,
            "FALLO DE SEGURIDAD: el usuario OPERADOR accedió a /admin/rutas sin autorización. " +
            "URL final: [" + urlFinal + "]. " +
            "Se esperaba redirección a /login, /error, o una respuesta 403/Access Denied."
        );
    }
}
