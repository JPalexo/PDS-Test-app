package com.j.c.qa.proyecto.tests;

import com.j.c.qa.proyecto.pages.LoginPage;
import com.j.c.qa.proyecto.pages.RutasPage;
import com.j.c.qa.proyecto.utils.BaseTest;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Pruebas de administración de rutas (/admin/rutas).
 * Requiere que el servidor Spring Boot esté corriendo en localhost:8080
 * y que la ruta ("Orizaba", "ruta 21") ya exista en la BD (sembrada por DataInitializer).
 */
public class RutasTest extends BaseTest {

    private static final String BASE_URL       = "http://localhost:8080";
    private static final String ADMIN_USUARIO  = "admin";
    private static final String ADMIN_PASSWORD = "00000000";

    /**
     * CP-ADM-01 (CP-10N) — Caso negativo: ruta duplicada.
     *
     * Precondición: la ruta ciudad="Orizaba" / nombreRuta="ruta 21" ya existe
     * en la BD (DataInitializer la siembra al arrancar).
     *
     * Flujo:
     *   1. Login como ADMIN.
     *   2. Navegar a /admin/rutas.
     *   3. Intentar guardar la misma ruta ("Orizaba", "ruta 21").
     *   4. Validar que el mensaje de error visible contiene "Ya existe".
     */
    @Test
    public void crearRutaDuplicada_CP10N() {
        // Paso 1 — autenticación
        LoginPage loginPage = new LoginPage(driver);
        loginPage.abrir(BASE_URL);
        loginPage.iniciarSesion(ADMIN_USUARIO, ADMIN_PASSWORD);

        wait.until(ExpectedConditions.urlContains("/admin/dashboard"));

        // Paso 2 — navegar a administración de rutas
        RutasPage rutasPage = new RutasPage(driver, wait);
        rutasPage.navegar(BASE_URL);

        wait.until(ExpectedConditions.titleContains("Rutas"));

        // Paso 3 — intentar crear ruta duplicada
        rutasPage.intentarCrearRuta("Orizaba", "ruta 21");

        // Paso 4 — validar mensaje de error post-redirect
        String mensajeError = rutasPage.obtenerMensajeError();

        Assert.assertTrue(
            mensajeError.toLowerCase().contains("ya existe"),
            "Se esperaba mensaje de ruta duplicada con 'ya existe'. " +
            "Mensaje real: [" + mensajeError + "]"
        );
    }
}
