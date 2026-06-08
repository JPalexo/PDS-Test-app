package com.j.c.qa.proyecto.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object para la vista de administración de rutas (/admin/rutas).
 * Encapsula selectores y acciones de Admin/rutas.html.
 */
public class RutasPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // <input type="text" id="ciudad" th:field="*{ciudad}">
    private final By inputCiudad = By.id("ciudad");

    // <input type="text" id="nombreRuta" th:field="*{nombreRuta}">
    private final By inputNombreRuta = By.id("nombreRuta");

    // <button type="submit">Guardar Ruta</button>
    private final By botonGuardarRuta = By.cssSelector("button[type='submit']");

    // <div th:if="${errorGuardado}" class="error-message" th:text="${errorGuardado}">
    private final By mensajeErrorGuardado = By.cssSelector("div.error-message");

    public RutasPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void navegar(String baseUrl) {
        driver.get(baseUrl + "/admin/rutas");
    }

    public void ingresarCiudad(String ciudad) {
        WebElement campo = driver.findElement(inputCiudad);
        campo.clear();
        campo.sendKeys(ciudad);
    }

    public void ingresarNombreRuta(String nombreRuta) {
        WebElement campo = driver.findElement(inputNombreRuta);
        campo.clear();
        campo.sendKeys(nombreRuta);
    }

    public void clicGuardarRuta() {
        driver.findElement(botonGuardarRuta).click();
    }

    /** Espera a que aparezca el mensaje de error post-redirect y retorna su texto. */
    public String obtenerMensajeError() {
        wait.until(ExpectedConditions.presenceOfElementLocated(mensajeErrorGuardado));
        return driver.findElement(mensajeErrorGuardado).getText();
    }

    /** Retorna true si el mensaje de error está presente en el DOM. */
    public boolean errorEstaVisible() {
        return !driver.findElements(mensajeErrorGuardado).isEmpty();
    }

    /**
     * Flujo completo: llena el formulario y hace clic en Guardar.
     * Después llamar a obtenerMensajeError() o errorEstaVisible().
     */
    public void intentarCrearRuta(String ciudad, String nombreRuta) {
        ingresarCiudad(ciudad);
        ingresarNombreRuta(nombreRuta);
        clicGuardarRuta();
    }
}
