package co.edu.unbosque.proyectofinal.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.Estado;
import co.edu.unbosque.proyectofinal.entity.Usuario;

public interface EstadoRepository extends JpaRepository<Estado, Long>{

	
	List<Estado> findByFechaExpiracionAfter(
            LocalDateTime fecha);

    /*
     * Obtener estados expirados.
     */
    List<Estado> findByFechaExpiracionBefore(
            LocalDateTime fecha);

    /*
     * Estados activos de un usuario.
     */
    List<Estado> findByUsuarioAndFechaExpiracionAfter(
            Usuario usuario,
            LocalDateTime fecha);

    /*
     * Estados ordenados del más reciente al más antiguo.
     */
    List<Estado> findByFechaExpiracionAfterOrderByFechaCreacionDesc(
            LocalDateTime fecha);

    /*
     * Estados de un usuario ordenados.
     */
    List<Estado> findByUsuarioAndFechaExpiracionAfterOrderByFechaCreacionDesc(
            Usuario usuario,
            LocalDateTime fecha);
	
}
