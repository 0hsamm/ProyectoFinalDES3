package co.edu.unbosque.proyectofinal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.TokenVerificacion;

public interface TokenVerificacionRepository extends JpaRepository<TokenVerificacion, Long>{

	Optional<TokenVerificacion> findByToken(String token);
	
}
