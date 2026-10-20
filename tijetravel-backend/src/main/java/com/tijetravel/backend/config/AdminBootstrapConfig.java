package com.tijetravel.backend.config;

import com.tijetravel.backend.model.Rol;
import com.tijetravel.backend.model.Sucursal;
import com.tijetravel.backend.model.Turista;
import com.tijetravel.backend.model.Usuario;
import com.tijetravel.backend.repository.SucursalRepository;
import com.tijetravel.backend.repository.TuristaRepository;
import com.tijetravel.backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class AdminBootstrapConfig {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapConfig.class);

    @Value("${admin.bootstrap.username:}")
    private String bootstrapUsername;

    @Value("${admin.bootstrap.password:}")
    private String bootstrapPassword;

    @Value("${admin.bootstrap.dni:}")
    private String bootstrapDni;

    @Bean
    public CommandLineRunner initAdminBootstrap(UsuarioRepository usuarioRepository,
                                               TuristaRepository turistaRepository,
                                               SucursalRepository sucursalRepository,
                                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (bootstrapUsername == null || bootstrapUsername.trim().isEmpty() ||
                bootstrapPassword == null || bootstrapPassword.trim().isEmpty() ||
                bootstrapDni == null || bootstrapDni.trim().isEmpty()) {
                log.info("Admin bootstrap no configurado por variables de entorno.");
                return;
            }

            String username = bootstrapUsername.trim();
            String dni = bootstrapDni.trim();

            if (usuarioRepository.existsByUsername(username) || usuarioRepository.existsByDni(dni)) {
                log.info("Admin bootstrap omitido: ya existe un usuario con username '{}' o DNI '{}'.", username, dni);
                return;
            }

            // Asegurar que exista al menos una sucursal para asociar turista
            List<Sucursal> sucursales = sucursalRepository.findAll();
            Sucursal sucursal = sucursales.isEmpty() ? null : sucursales.get(0);
            if (sucursal == null) {
                sucursal = new Sucursal();
                sucursal.setCodigo("SUC-CENTRAL");
                sucursal.setCiudad("Buenos Aires");
                sucursal.setDireccion("Casa Central 100");
                sucursal.setTelefono("011-0000-0000");
                sucursal = sucursalRepository.save(sucursal);
            }

            // Crear turista asociado al admin si no existe
            if (!turistaRepository.existsByDni(dni)) {
                Turista turistaAdmin = new Turista();
                turistaAdmin.setCodigo("TUR-ADM-" + dni);
                turistaAdmin.setDni(dni);
                turistaAdmin.setNombre("Administrador");
                turistaAdmin.setApellido("Sistema");
                turistaAdmin.setDireccion("Sede Central");
                turistaAdmin.setEmail("admin@tijetravel.com");
                turistaAdmin.setTelefonoCelular("011-15-0000-0000");
                turistaAdmin.setSucursal(sucursal);
                turistaRepository.save(turistaAdmin);
            }

            Usuario adminUser = new Usuario();
            adminUser.setCodigo("USR-ADM-01");
            adminUser.setUsername(username);
            adminUser.setPassword(passwordEncoder.encode(bootstrapPassword.trim()));
            adminUser.setRol(Rol.ADMINISTRADOR);
            adminUser.setDni(dni);

            usuarioRepository.save(adminUser);
            log.info("Admin bootstrap ejecutado con éxito. Usuario administrador temporal '{}' creado.", username);
        };
    }
}

