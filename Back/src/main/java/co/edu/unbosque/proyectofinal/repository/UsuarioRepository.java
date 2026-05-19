package co.edu.unbosque.proyectofinal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.proyectofinal.entity.Usuario;

@Repository
public interface UsuarioRepository
extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByUsuario(
			String usuario);

	Optional<Usuario> findByCorreo(
			String correo);

}