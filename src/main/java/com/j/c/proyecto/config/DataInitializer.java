package com.j.c.proyecto.config;

import com.j.c.proyecto.model.Descuento;
import com.j.c.proyecto.model.Rol;
import com.j.c.proyecto.model.Ruta;
import com.j.c.proyecto.model.Tarifa;
import com.j.c.proyecto.model.Usuario;
import com.j.c.proyecto.repository.DescuentoRepository;
import com.j.c.proyecto.repository.RutaRepository;
import com.j.c.proyecto.repository.TarifaRepository;
import com.j.c.proyecto.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    private final RutaRepository rutaRepository;
    private final TarifaRepository tarifaRepository;
    private final DescuentoRepository descuentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RutaRepository rutaRepository,
                           TarifaRepository tarifaRepository,
                           DescuentoRepository descuentoRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.rutaRepository = rutaRepository;
        this.tarifaRepository = tarifaRepository;
        this.descuentoRepository = descuentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initializeSeedData() {
        return args -> {
            seedRutaConTarifa("Orizaba", "ruta 21", new BigDecimal("80.00"));
            seedDescuento("Estudiante",    new BigDecimal("0.10"));
            seedDescuento("Tercera Edad", new BigDecimal("0.15"));
            seedOperador("pedro", "12345678", "pedro@correo.com");
        };
    }

    private void seedRutaConTarifa(String ciudad, String nombreRuta, BigDecimal precio) {
        Ruta ruta = rutaRepository
                .findByCiudadAndNombreRuta(ciudad, nombreRuta)
                .orElseGet(() -> {
                    Ruta nueva = new Ruta(ciudad, nombreRuta);
                    rutaRepository.save(nueva);
                    System.out.println("Ruta semilla creada: " + ciudad + " / " + nombreRuta);
                    return nueva;
                });

        if (tarifaRepository.findByRuta(ruta).isEmpty()) {
            tarifaRepository.save(new Tarifa(ruta, precio));
            System.out.println("Tarifa semilla creada para '" + nombreRuta + "': $" + precio);
        }
    }

    private void seedDescuento(String nombre, BigDecimal porcentaje) {
        if (descuentoRepository.findByNombre(nombre).isEmpty()) {
            descuentoRepository.save(new Descuento(null, nombre, porcentaje));
            System.out.printf("Descuento semilla creado: %s (%.0f%%)%n",
                    nombre, porcentaje.multiply(BigDecimal.valueOf(100)));
        }
    }

    private void seedOperador(String username, String password, String email) {
        if (!usuarioRepository.existsByUsername(username)) {
            Usuario operador = new Usuario();
            operador.setUsername(username);
            operador.setPassword(passwordEncoder.encode(password));
            operador.setEmail(email);
            operador.setRol(Rol.USUARIO);
            usuarioRepository.save(operador);
            System.out.println("Usuario operador semilla creado: " + username);
        }
    }
}
