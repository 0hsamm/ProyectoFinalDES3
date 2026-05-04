package co.edu.unbosque.proyectofinal.repository;

import java.util.Optional;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	Optional<Usuario> findByUsuario(String usuario);
	
}
