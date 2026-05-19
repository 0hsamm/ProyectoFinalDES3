package co.edu.unbosque.proyectofinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.InteraccionIa;

/**
 * Acceso a los registros de interaccion con IA.
 */
public interface InteraccionIaRepository
        extends JpaRepository<InteraccionIa, Long> {

    List<InteraccionIa> findTop20ByUsuario_IdOrderByFechaCreacionDesc(
            Long usuarioId);
}
