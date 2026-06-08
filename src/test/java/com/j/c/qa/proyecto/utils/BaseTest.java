package com.j.c.qa.proyecto.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase base para todas las pruebas de UI.
 * Centraliza la inicialización y liberación del WebDriver,
 * evitando repetir configuración en cada clase de prueba.
 */
public class BaseTest {

    // Instancia compartida del navegador; protected para que las subclases puedan acceder
    protected WebDriver driver;

    // Espera explícita reutilizable en todos los tests (15 s máximo por elemento)
    protected WebDriverWait wait;

    /**
     * Se ejecuta antes de cada método de prueba.
     * Configura ChromeDriver, maximiza la ventana y establece espera implícita.
     */
    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Deshabilitar el gestor de contraseñas y sus alertas de brechas de seguridad.
        // Sin esto, Chrome muestra ventanas emergentes nativas que interrumpen el flujo
        // de los tests cuando detecta contraseñas usadas en pruebas como "comprometidas".
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-features=PasswordLeakDetection");

        driver = new ChromeDriver(options);

        // Maximizar para asegurar visibilidad completa de los elementos de la UI
        driver.manage().window().maximize();

        // WebDriverWait explícito: más confiable que implicitlyWait con ChromeDriver moderno.
        // Usado en VentaPage y demás Page Objects para aguardar recargas de página.
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Se ejecuta después de cada método de prueba.
     * Cierra el navegador y libera los recursos del WebDriver.
     */
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
