package com.j.c.qa.proyecto.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class VentaPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By selectCiudad = By.id("ciudadSeleccionada");
    private final By selectRuta = By.id("rutaSeleccionada");
    private final By selectDescuento = By.id("descuentoSeleccionado");
    private final By inputMontoRecibido = By.id("montoRecibido");
    private final By botonCalcularCambio =
            By.xpath("//button[@type='submit' and normalize-space(text())='Calcular Cambio']");
    private final By botonRegistrarVenta =
            By.xpath("//button[@type='submit' and normalize-space(text())='Registrar Venta']");
    private final By mensajeExito = By.cssSelector("div.success-message");
    private final By mensajeError = By.cssSelector("div.error-message");
    private final By spanTarifaOriginal = By.xpath(
        "//div[contains(@class,'output-group')][.//label[normalize-space(text())='Tarifa Original:']]/span"
    );
    private final By spanTarifaConDescuento = By.xpath(
        "//div[contains(@class,'output-group')][.//label[normalize-space(text())='Tarifa con Descuento:']]/span"
    );
    private final By spanCambio = By.xpath(
        "//div[contains(@class,'output-group')][.//label[normalize-space(text())='Cambio a Entregar:']]/span"
    );

    public VentaPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait   = wait;
    }

    public void abrir(String baseUrl) {
        driver.get(baseUrl + "/usuario/venta");
    }

    public void seleccionarCiudad(String ciudad) {
        wait.until(ExpectedConditions.elementToBeClickable(selectCiudad));
        new Select(driver.findElement(selectCiudad)).selectByVisibleText(ciudad);
        wait.until(ExpectedConditions.presenceOfElementLocated(selectRuta));
    }

    public void seleccionarRuta(String nombreRuta) {
        wait.until(ExpectedConditions.elementToBeClickable(selectRuta));
        new Select(driver.findElement(selectRuta)).selectByVisibleText(nombreRuta);
        wait.until(ExpectedConditions.presenceOfElementLocated(inputMontoRecibido));
    }

    public void seleccionarDescuento(String nombreDescuento) {
        wait.until(ExpectedConditions.elementToBeClickable(selectDescuento));
        new Select(driver.findElement(selectDescuento)).selectByVisibleText(nombreDescuento);
        wait.until(ExpectedConditions.presenceOfElementLocated(inputMontoRecibido));
    }

    public String seleccionarPrimerDescuento() {
        wait.until(ExpectedConditions.elementToBeClickable(selectDescuento));
        Select select = new Select(driver.findElement(selectDescuento));
        List<WebElement> opciones = select.getOptions();
        String textoOpcion = opciones.get(1).getText();
        select.selectByIndex(1);
        wait.until(ExpectedConditions.presenceOfElementLocated(inputMontoRecibido));
        return textoOpcion;
    }

    public void ingresarMontoRecibido(String monto) {
        WebElement campo = wait.until(ExpectedConditions.elementToBeClickable(inputMontoRecibido));
        campo.clear();
        campo.sendKeys(monto);
    }

    public void clicCalcularCambio() {
        wait.until(ExpectedConditions.elementToBeClickable(botonCalcularCambio)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(botonRegistrarVenta));
    }

    public void intentarCalcularCambioSinEsperar() {
        wait.until(ExpectedConditions.elementToBeClickable(botonCalcularCambio)).click();
    }

    public void clicRegistrarVenta() {
        wait.until(ExpectedConditions.elementToBeClickable(botonRegistrarVenta)).click();
    }

    public String obtenerMensajeExito() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(mensajeExito)).getText();
    }

    public boolean exitoEstaVisible() {
        return !driver.findElements(mensajeExito).isEmpty();
    }

    public String obtenerMensajeError() {
        return driver.findElement(mensajeError).getText();
    }

    public boolean errorEstaVisible() {
        return !driver.findElements(mensajeError).isEmpty();
    }

    public String obtenerTextoTarifaOriginal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(spanTarifaOriginal)).getText();
    }

    public String obtenerTextoTarifaConDescuento() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(spanTarifaConDescuento)).getText();
    }

    public String obtenerTextoCambio() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(spanCambio)).getText();
    }

    public boolean registrarVentaEstaVisible() {
        return !driver.findElements(botonRegistrarVenta).isEmpty();
    }

    public String obtenerMensajeValidacionMonto() {
        WebElement campo = driver.findElement(inputMontoRecibido);
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", campo);
    }
}
