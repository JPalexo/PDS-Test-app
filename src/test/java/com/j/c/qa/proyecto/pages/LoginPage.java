package com.j.c.qa.proyecto.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object para la pantalla de autenticación (/login).
 * Encapsula todos los selectores y acciones de la vista login.html.
 */
public class LoginPage {

    private final WebDriver driver;

    // --- Selectores extraídos del DOM de login.html ---

    // <input type="text" id="username" name="username">
    private final By campoUsuario = By.id("username");

    // <input type="password" id="password" name="password">
    private final By campoPassword = By.id("password");

    // <button type="submit">Iniciar Sesión</button>  (sin id, se usa atributo)
    private final By botonIniciarSesion = By.cssSelector("button[type='submit']");

    // <div th:if="${error}" class="message error"><p th:text="${error}"></p></div>
    // Este elemento solo existe en el DOM cuando Spring Security envía un error de autenticación
    private final By mensajeError = By.cssSelector("div.message.error > p");

    // -------------------------------------------------------

    /**
     * @param driver instancia de WebDriver inicializada en BaseTest
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    /** Navega a la página de login. */
    public void abrir(String baseUrl) {
        driver.get(baseUrl + "/login");
    }

    /** Escribe el nombre de usuario en el campo correspondiente. */
    public void ingresarUsuario(String usuario) {
        WebElement campo = driver.findElement(campoUsuario);
        campo.clear();
        campo.sendKeys(usuario);
    }

    /** Escribe la contraseña en el campo correspondiente. */
    public void ingresarPassword(String password) {
        WebElement campo = driver.findElement(campoPassword);
        campo.clear();
        campo.sendKeys(password);
    }

    /** Hace clic en el botón 'Iniciar Sesión'. */
    public void clicIniciarSesion() {
        driver.findElement(botonIniciarSesion).click();
    }

    /**
     * Método transaccional que combina las tres acciones del flujo de login.
     *
     * @param usuario   nombre de usuario
     * @param password  contraseña
     */
    public void iniciarSesion(String usuario, String password) {
        ingresarUsuario(usuario);
        ingresarPassword(password);
        clicIniciarSesion();
    }

    /**
     * Retorna el texto del mensaje de error visible en pantalla.
     * Llamar solo cuando se espera que el error esté presente;
     * de lo contrario lanzará NoSuchElementException.
     *
     * @return texto del mensaje (ej: "Credenciales inválidas", "Cuenta bloqueada")
     */
    public String obtenerMensajeError() {
        return driver.findElement(mensajeError).getText();
    }

    /**
     * Indica si el bloque de error está actualmente visible en el DOM.
     * Usa findElements para evitar NoSuchElementException cuando no hay error.
     */
    public boolean errorEstaVisible() {
        return !driver.findElements(mensajeError).isEmpty();
    }
}
