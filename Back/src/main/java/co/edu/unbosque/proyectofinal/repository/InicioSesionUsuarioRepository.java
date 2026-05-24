package co.edu.unbosque.proyectofinal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.InicioSesionUsuario;

public interface InicioSesionUsuarioRepository extends JpaRepository<InicioSesionUsuario, Long> {

    Optional<InicioSesionUsuario> findByToken(String token);
}