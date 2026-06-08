package com.j.c.qa.proyecto.tests;

import com.j.c.qa.proyecto.pages.LoginPage;
import com.j.c.qa.proyecto.pages.VentaPage;
import com.j.c.qa.proyecto.utils.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

public class VentaTest extends BaseTest {

    private static final String BASE_URL            = "http://localhost:8080";
    private static final String OPERADOR_USUARIO    = "pedro";
    private static final String OPERADOR_PASSWORD   = "12345678";
    private static final String CIUDAD              = "Orizaba";
    private static final String RUTA                = "ruta 21";
    private static final String MONTO_VALIDO        = "100";
    private static final String MONTO_CON_DESCUENTO = "200";
    private static final String MSG_EXITO           = "Venta registrada exitosamente.";

    private void loginComoOperador() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.abrir(BASE_URL);
        loginPage.iniciarSesion(OPERADOR_USUARIO, OPERADOR_PASSWORD);
        Assert.assertTrue(
            driver.getCurrentUrl().contains("/usuario/venta"),
            "Login fallido. URL: " + driver.getCurrentUrl() + ". Verificar usuario pedro con rol USUARIO."
        );
    }

    @Test
    public void ventaConRutaValida_CP01P() {
        loginComoOperador();
        VentaPage ventaPage = new VentaPage(driver, wait);
        ventaPage.seleccionarCiudad(CIUDAD);
        ventaPage.seleccionarRuta(RUTA);
        ventaPage.ingresarMontoRecibido(MONTO_VALIDO);
        ventaPage.clicCalcularCambio();
        ventaPage.clicRegistrarVenta();
        Assert.assertTrue(ventaPage.exitoEstaVisible(), "Mensaje de exito no visible tras registrar venta");
        Assert.assertEquals(ventaPage.obtenerMensajeExito(), MSG_EXITO, "Texto del mensaje de exito no coincide");
    }

    @Test
    public void ventaSinRuta_CP01N() {
        loginComoOperador();
        VentaPage ventaPage = new VentaPage(driver, wait);
        ventaPage.seleccionarCiudad(CIUDAD);
        boolean registrarVisible = !driver.findElements(
            By.xpath("//button[@type='submit' and normalize-space(text())='Registrar Venta']")).isEmpty();
        Assert.assertFalse(registrarVisible, "Boton Registrar Venta no deberia existir sin ruta");
        boolean calcularVisible = !driver.findElements(
            By.xpath("//button[@type='submit' and normalize-space(text())='Calcular Cambio']")).isEmpty();
        Assert.assertFalse(calcularVisible, "Boton Calcular Cambio no deberia existir sin ruta");
    }

    @Test
    public void aplicarDescuentoEstudiante_CP02P() {
        loginComoOperador();
        VentaPage ventaPage = new VentaPage(driver, wait);
        ventaPage.seleccionarCiudad(CIUDAD);
        ventaPage.seleccionarRuta(RUTA);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("descuentoSeleccionado")));
        List<WebElement> opciones = new Select(driver.findElement(By.id("descuentoSeleccionado"))).getOptions();
        if (opciones.size() <= 1) {
            throw new SkipException(
                "No hay descuentos registrados en la BD. " +
                "Agregar al menos uno desde /admin/descuentos y reejecutar la suite."
            );
        }

        String descuentoSeleccionado = ventaPage.seleccionarPrimerDescuento();
        String textoTarifaConDescuento = ventaPage.obtenerTextoTarifaConDescuento();
        Assert.assertNotNull(textoTarifaConDescuento,
            "La tarifa con descuento deberia ser visible tras aplicar: " + descuentoSeleccionado);

        ventaPage.ingresarMontoRecibido(MONTO_CON_DESCUENTO);
        ventaPage.clicCalcularCambio();
        String textoCambio = ventaPage.obtenerTextoCambio();

        BigDecimal monto              = new BigDecimal(MONTO_CON_DESCUENTO);
        BigDecimal tarifaConDescuento = new BigDecimal(textoTarifaConDescuento.replace(",", ""));
        BigDecimal cambioEsperado     = monto.subtract(tarifaConDescuento);
        BigDecimal cambioReal         = new BigDecimal(textoCambio.replace(",", ""));
        Assert.assertEquals(cambioReal.compareTo(cambioEsperado), 0,
            "Cambio incorrecto. Esperado=" + cambioEsperado + " Obtenido=" + cambioReal +
            " | Descuento=" + descuentoSeleccionado + " | TarifaEfectiva=" + textoTarifaConDescuento);
    }

    @Test
    public void validarDatosIncompletos_CP07N() {
        loginComoOperador();
        VentaPage ventaPage = new VentaPage(driver, wait);
        ventaPage.seleccionarCiudad(CIUDAD);
        ventaPage.seleccionarRuta(RUTA);
        ventaPage.intentarCalcularCambioSinEsperar();
        String mensajeValidacion = ventaPage.obtenerMensajeValidacionMonto();
        Assert.assertFalse(mensajeValidacion.isEmpty(),
            "El campo montoRecibido deberia reportar validacion HTML5 cuando esta vacio");
        Assert.assertFalse(ventaPage.registrarVentaEstaVisible(),
            "El boton Registrar Venta no deberia aparecer cuando el monto esta vacio");
    }
}