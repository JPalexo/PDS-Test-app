# Reporte de Ejecución de Pruebas Automatizadas (Selenium E2E)

**Proyecto:** Sistema de Gestión de Transporte y Venta de Boletos  
**Materia:** Fundamentos de Desarrollo y Pruebas de Software (FDPDS) — 6to Semestre  
**Autor:** JPalexo · bermudezbaezalejandro@gmail.com

---

## 1. Fecha y Entorno de Ejecución

| Parámetro              | Valor                                      |
|------------------------|--------------------------------------------|
| Fecha de ejecución     | 2026-06-08                                 |
| Hora de inicio (CST)   | 17:13:27                                   |
| Hora de fin (CST)      | 17:13:38                                   |
| Duración suite (Surefire) | 11.336s                                 |
| Duración total (Maven) | 22.804s                                    |
| Comando ejecutado      | `mvnw.cmd test`                            |
| Sistema operativo      | Windows 11 Pro 10.0.26200                  |
| JDK                    | Java 21 (compilación forzada vía `pom.xml`)|
| Framework de pruebas   | **TestNG 7.10.2**                          |
| Framework de UI        | **Selenium Java 4.22.0**                   |
| Gestión de drivers     | WebDriverManager 5.9.2                     |
| Navegador              | Google Chrome 148.0.7778.217               |
| ChromeDriver           | 148.0.7778.178                             |
| Plugin Surefire        | maven-surefire-plugin 3.1.2                |
| Stack de la aplicación | Spring Boot 3.2.4 · PostgreSQL · Thymeleaf |
| URL base               | `http://localhost:8080`                    |

---

## 2. Resumen Ejecutivo

| Métrica                    | Valor      |
|----------------------------|------------|
| **Total de casos ejecutados** | **8**   |
| Casos PASS                 | **7**      |
| Casos FAIL                 | **1**      |
| Casos SKIP                 | **0**      |
| **Tasa de éxito global**   | **87.5 %** |
| FAIL intencional (deuda técnica) | 1    |
| FAIL real (regresión)      | **0**      |

> El único caso fallido (`CP-SEG-01`) corresponde al requisito funcional **RF-011** (bloqueo de cuenta por intentos fallidos), cuya implementación en el backend está pendiente. El test fue diseñado como regresión activa: **pasará automáticamente** una vez que se implemente RF-011. No existe ninguna regresión no esperada.

---

## 3. Tabla de Resultados

| ID del Caso | Nombre de la Prueba (Método Java) | Clase de Test | Estado | Tiempo (ms) | Observaciones |
|-------------|-----------------------------------|---------------|--------|-------------|---------------|
| CP-SEG-00 | `loginExitoso` | `LoginTest` | ✅ PASS | 401 ms | Login ADMIN → redirección a `/admin/dashboard` validada. Título `"Panel de Administración"` confirmado en DOM. |
| CP-SEG-01 | `validarBloqueoPorIntentosFallidos` | `LoginTest` | ❌ FAIL | 767 ms | **Falla intencional.** RF-011 no implementado. El sistema responde `"Nombre de usuario o contraseña incorrectos"` en vez de un mensaje de bloqueo. Ver §5. |
| CP-SEG-02 | `escalamientoDePrivilegios_CP08N` | `SeguridadRolesTest` | ✅ PASS | 355 ms | Usuario OPERADOR (`pedro`) bloqueado al acceder `/admin/rutas`. Spring Security devolvió 403 / redirección fuera de `/admin`. |
| CP-VEN-01 | `ventaConRutaValida_CP01P` | `VentaTest` | ✅ PASS | 775 ms | Flujo completo: ciudad → ruta → monto → Calcular Cambio → Registrar Venta. Mensaje de éxito confirmado en DOM. |
| CP-VEN-02 | `ventaSinRuta_CP01N` | `VentaTest` | ✅ PASS | 417 ms | Sin ruta seleccionada: botones `"Calcular Cambio"` y `"Registrar Venta"` ausentes del DOM (`th:if` Thymeleaf). |
| CP-VEN-03 | `aplicarDescuentoEstudiante_CP02P` | `VentaTest` | ✅ PASS | 874 ms | Descuento `"Estudiante"` (10%) sobre tarifa $80.00 → $72.00. Cambio con monto $200 → $128.00. Aritmética `BigDecimal` validada. |
| CP-VEN-04 | `validarDatosIncompletos_CP07N` | `VentaTest` | ✅ PASS | 572 ms | Campo `montoRecibido` vacío: HTML5 `required` bloquea submit. `validationMessage` verificado vía `JavascriptExecutor`. |
| CP-ADM-01 | `crearRutaDuplicada_CP10N` | `RutasTest` | ✅ PASS | 522 ms | Intento de crear ruta duplicada (`"Orizaba"` / `"ruta 21"`): mensaje `"Ya existe una ruta con la ciudad..."` renderizado vía `errorGuardado`. |

---

## 4. Cobertura de Requisitos Funcionales

| RF     | Descripción                                                       | Estado Backend     | Caso(s) asociado(s) | Resultado |
|--------|-------------------------------------------------------------------|--------------------|---------------------|-----------|
| RF-010 | Login con credenciales válidas redirige al dashboard por rol      | ✅ Implementado    | CP-SEG-00           | ✅ PASS   |
| RF-011 | Bloqueo de cuenta tras N intentos fallidos consecutivos           | ❌ No implementado | CP-SEG-01           | ❌ FAIL   |
| RF-012 | Control de acceso por rol (sin escalamiento de privilegios)       | ✅ Implementado    | CP-SEG-02           | ✅ PASS   |
| RF-020 | Venta de boleto: selección de ciudad, ruta, monto y registro      | ✅ Implementado    | CP-VEN-01           | ✅ PASS   |
| RF-021 | Formulario de venta no avanza sin ruta seleccionada               | ✅ Implementado    | CP-VEN-02           | ✅ PASS   |
| RF-022 | Aplicación de descuento reduce la tarifa correctamente            | ✅ Implementado    | CP-VEN-03           | ✅ PASS   |
| RF-023 | Campo monto es obligatorio; HTML5 bloquea el envío si está vacío  | ✅ Implementado    | CP-VEN-04           | ✅ PASS   |
| RF-030 | Administración de rutas: rechazo de ruta duplicada con mensaje    | ✅ Implementado    | CP-ADM-01           | ✅ PASS   |

---

## 5. Conclusión de QA

Los flujos críticos de negocio del sistema de gestión de transporte fueron verificados de extremo a extremo mediante una suite automatizada de **8 casos de prueba Selenium E2E**. Los módulos de **autenticación** (`/login`), **venta de boletos** (`/usuario/venta`) y **administración de rutas** (`/admin/rutas`) operan conforme a los requisitos funcionales especificados.

La seguridad del sistema fue validada en dos dimensiones: el acceso legítimo de administrador fue confirmado (CP-SEG-00) y la barrera de control de acceso por rol fue verificada con éxito, acreditando que un usuario con rol OPERADOR no puede escalar privilegios hacia rutas administrativas (CP-SEG-02).

### Deuda técnica — RF-011 (Bloqueo por intentos fallidos)

El caso **CP-SEG-01** falla de forma **intencional y documentada**. El backend no implementa el mecanismo de bloqueo de cuenta definido en RF-011: `Usuario.java` retorna `isAccountNonLocked() = true` de forma fija, sin campo de conteo de intentos, y `SecurityConfig.java` no registra ningún `AuthenticationFailureHandler`. El test permanece activo en la suite como **regresión preventiva**: detectará automáticamente cuando RF-011 sea implementado y pasará sin ningún cambio en el código de prueba.

### Estado final de la suite

| Módulo cubierto | Tests | PASS | FAIL |
|-----------------|-------|------|------|
| Seguridad / Autenticación | 3 | 2 | 1 (RF-011) |
| Venta de Boletos (Operador) | 4 | 4 | 0 |
| Administración de Rutas | 1 | 1 | 0 |
| **Total** | **8** | **7** | **1** |

**Tasa de éxito sobre requisitos implementados: 100 % (7/7).**

---

## 6. Infraestructura de Pruebas

| Componente | Detalle | Estado |
|------------|---------|--------|
| Compilación Java 21 + Lombok | `javac` forzado desde `pom.xml` | OK |
| WebDriverManager — descarga ChromeDriver 148 | Automática, sin instalación manual | OK |
| `@BeforeMethod setUp()` — ChromeOptions | Popup gestor de contraseñas deshabilitado | OK (×8) |
| `@AfterMethod tearDown()` — `driver.quit()` | Liberación del navegador tras cada test | OK (×8) |
| `WebDriverWait` explícita (15s) | Sustituye `implicitlyWait` — compatible con Chrome 148+ | OK |
| Formulario multi-step Thymeleaf | Esperas explícitas tras cada recarga JS | OK |
| Proveedor TestNG en Surefire 3.1.2 | 8 tests detectados y ejecutados | OK |

**Nota técnica:** Chrome 148 genera una advertencia de compatibilidad CDP (`Unable to find version of CDP to use for 148.0.7778.217`). Esta advertencia **no afecta la ejecución de los tests** ya que ninguno de los casos utiliza funcionalidades del Chrome DevTools Protocol. Se resolvería actualizando la dependencia a `selenium-devtools-v148` en `pom.xml`, pero no es bloqueante para el entregable actual.

---

*Reporte generado a partir de `target/surefire-reports/testng-results.xml` — ejecución 2026-06-08T17:13:27 CST*  
*Proyecto FDPDS — 6to Semestre*
