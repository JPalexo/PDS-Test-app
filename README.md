# Sistema de Gestión de Transporte y Venta de Boletos

Aplicación web MVC desarrollada con Spring Boot para la venta de boletos de autobús.  
Proyecto académico — FDPDS 6to Semestre.

---

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Backend | Spring Boot 3.2.4 · Java 21 |
| Seguridad | Spring Security (form login, roles) |
| Persistencia | Spring Data JPA · PostgreSQL |
| Vistas | Thymeleaf |
| Utilidades | Lombok |
| Pruebas E2E | Selenium Java 4.22.0 · TestNG 7.10.2 · WebDriverManager 5.9.2 |

---

## Requisitos previos

| Herramienta | Versión mínima | Notas |
|-------------|----------------|-------|
| Java (JDK) | 21 | `JAVA_HOME` debe apuntar a JDK 21 |
| Maven | 3.9+ | Incluido en el proyecto como wrapper (`mvnw.cmd`) |
| PostgreSQL | 14+ | Debe estar corriendo en `localhost:5432` |
| Google Chrome | Cualquier versión reciente | ChromeDriver se descarga automáticamente |

---

## 1. Configurar la base de datos

Crear la base de datos antes de arrancar la aplicación:

```sql
CREATE DATABASE jc_db;
```

Parámetros de conexión (configurados en `src/main/resources/application.properties`):

| Parámetro | Valor |
|-----------|-------|
| Host | `localhost:5432` |
| Base de datos | `jc_db` |
| Usuario | `postgres` |
| Contraseña | `1255` |

> El esquema se genera automáticamente al arrancar (`ddl-auto=update`).  
> No es necesario ejecutar scripts SQL manualmente.

---

## 2. Arrancar la aplicación

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Al arrancar, dos inicializadores siembran datos de prueba automáticamente:

- **`AdminInitializer`** — crea el usuario administrador si no existe.
- **`DataInitializer`** — crea la ruta, tarifas, descuentos y usuario operador de prueba si no existen.

Usuarios disponibles tras el primer arranque:

| Usuario | Contraseña | Rol | Redirige a |
|---------|-----------|-----|-----------|
| `admin` | `00000000` | ADMIN | `/admin/dashboard` |
| `pedro` | `12345678` | USUARIO | `/usuario/venta` |

Verificar que la app responde en: `http://localhost:8080/login`

---

## 3. Ejecutar la suite de pruebas E2E

> **La aplicación debe estar corriendo** (paso 2) antes de ejecutar los tests.  
> Los tests son Selenium end-to-end: abren Chrome real y navegan la app.

### Ejecutar toda la suite

```bash
# Windows
mvnw.cmd test

# Linux / macOS
./mvnw test
```

### Ejecutar una sola clase

```bash
mvnw.cmd test -Dtest=LoginTest
mvnw.cmd test -Dtest=VentaTest
mvnw.cmd test -Dtest=RutasTest
mvnw.cmd test -Dtest=SeguridadRolesTest
```

### Resultado esperado

```
Tests run: 8, Failures: 1, Errors: 0, Skipped: 0
```

> El único FAIL esperado es `validarBloqueoPorIntentosFallidos` (CP-SEG-01).  
> Este test verifica el requisito RF-011 (bloqueo por intentos fallidos) que **aún no está implementado en el backend**.  
> Es una regresión activa intencional: pasará automáticamente cuando se implemente RF-011.  
> **No indica un error en la configuración del entorno.**

---

## 4. Ver los reportes de Surefire

Los reportes XML y TXT se generan en:

```
target/surefire-reports/
  testng-results.xml      ← tiempos y stack traces por test
  TEST-TestSuite.xml      ← formato JUnit compatible
```

El reporte de ejecución completo (Markdown) está en:

```
Reporte_Ejecucion_QA.md
```

---

## 5. Casos de prueba implementados

| ID | Método | Módulo | Tipo | Descripción |
|----|--------|--------|------|-------------|
| CP-SEG-00 | `LoginTest.loginExitoso` | Autenticación | Positivo | Login ADMIN → redirección a dashboard |
| CP-SEG-01 | `LoginTest.validarBloqueoPorIntentosFallidos` | Autenticación | Negativo | 3 intentos fallidos → mensaje de bloqueo *(FAIL intencional)* |
| CP-SEG-02 | `SeguridadRolesTest.escalamientoDePrivilegios_CP08N` | Seguridad | Negativo | OPERADOR bloqueado al acceder rutas de ADMIN |
| CP-VEN-01 | `VentaTest.ventaConRutaValida_CP01P` | Venta | Positivo | Flujo completo de venta de boleto |
| CP-VEN-02 | `VentaTest.ventaSinRuta_CP01N` | Venta | Negativo | Formulario no avanza sin ruta seleccionada |
| CP-VEN-03 | `VentaTest.aplicarDescuentoEstudiante_CP02P` | Venta | Positivo | Aritmética de descuento validada con `BigDecimal` |
| CP-VEN-04 | `VentaTest.validarDatosIncompletos_CP07N` | Venta | Negativo | HTML5 `required` bloquea submit sin monto |
| CP-ADM-01 | `RutasTest.crearRutaDuplicada_CP10N` | Admin Rutas | Negativo | Mensaje de error al intentar crear ruta duplicada |

---

## 6. Estructura de la suite de pruebas

```
src/test/java/com/j/c/qa/proyecto/
├── utils/
│   └── BaseTest.java          # WebDriver setup/teardown, ChromeOptions, WebDriverWait
├── pages/
│   ├── LoginPage.java         # Page Object de /login
│   ├── VentaPage.java         # Page Object de /usuario/venta (formulario multi-step)
│   └── RutasPage.java         # Page Object de /admin/rutas
└── tests/
    ├── LoginTest.java          # CP-SEG-00, CP-SEG-01
    ├── VentaTest.java          # CP-VEN-01..04
    ├── RutasTest.java          # CP-ADM-01
    └── SeguridadRolesTest.java # CP-SEG-02
```

---

## 7. Solución de problemas frecuentes

**Chrome no abre / test falla inmediatamente**  
Verificar que Google Chrome esté instalado. WebDriverManager descarga el ChromeDriver compatible automáticamente.

**`Connection refused` al arrancar los tests**  
La aplicación no está corriendo. Ejecutar `mvnw.cmd spring-boot:run` en otra terminal antes de lanzar los tests.

**Error de compilación `illegal character: '﻿'`**  
Un archivo `.java` fue guardado con encoding UTF-16 LE (BOM). Reescribir el archivo con encoding UTF-8.

**Tests fallan por datos no encontrados en BD**  
`DataInitializer` no pudo crear los datos semilla. Verificar que PostgreSQL esté corriendo y que la BD `jc_db` exista con las credenciales correctas.

---

## 8. Arquitectura de la aplicación

```
src/main/java/com/j/c/proyecto/
├── config/         # AdminInitializer, DataInitializer (datos semilla)
├── controller/
│   ├── admin/      # Rutas, Tarifas, Descuentos, Empleados, Reportes
│   └── usuario/    # Venta de boletos
├── dto/            # Form binding y transferencia de datos
├── exception/      # Jerarquía de excepciones propias
├── model/          # Entidades JPA (Usuario, Ruta, Tarifa, Descuento, Venta, Rol)
├── repository/     # Interfaces Spring Data JPA
├── security/       # SecurityConfig, SuccessHandler, UserDetailsService
└── service/        # Lógica de negocio con @Transactional
```
