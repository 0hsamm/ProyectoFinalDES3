package co.edu.unbosque.proyectofinal.configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.proyectofinal.entity.Usuario;

import co.edu.unbosque.proyectofinal.enums.RolUsuario;

import co.edu.unbosque.proyectofinal.repository.UsuarioRepository;

@Configuration
public class LoadDatabase {

    private static final Logger log =

            LoggerFactory.getLogger(
                    LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(

            UsuarioRepository usuarioRepo,

            PasswordEncoder passwordEncoder) {

        return args -> {

            Optional<Usuario> adminEncontrado =

                    usuarioRepo.findByUsuario(
                            "admin");

            if (
                    adminEncontrado.isPresent()
            ) {

                log.info(
                        "El administrador ya existe");

            } else {

                Usuario admin =
                        new Usuario();

                admin.setUsuario(
                        "admin");

                admin.setCorreo(
                        "admin@reload.com");

                admin.setNombrePersona(
                        "Administrador Reload");

                admin.setContrasenaHash(

                        passwordEncoder.encode(
                                "Admin123*"
                        )
                );

                admin.setFechaNacimiento(

                        LocalDate.of(
                                2000,
                                1,
                                1
                        )
                );

                admin.setFechaCreacionCuenta(
                        LocalDateTime.now()
                );

                admin.setUltimaVezEnLinea(
                        LocalDateTime.now()
                );

                admin.setEnLinea(
                        false
                );

                admin.setHabilitado(
                        true
                );

                admin.setRol(
                        RolUsuario.ROLE_ADMIN
                );

                usuarioRepo.save(
                        admin
                );

                log.info(
                        "Administrador creado correctamente");
            }
        };
    }
}