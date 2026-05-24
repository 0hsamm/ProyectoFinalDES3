package co.edu.unbosque.proyectofinal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.proyectofinal.entity.Llamada;
import co.edu.unbosque.proyectofinal.enums.EstadoLlamada;

@Repository
public interface LlamadaRepository extends JpaRepository<Llamada, Long> {

	// Buscar llamada por nombre del canal de Agora
	Optional<Llamada> findByCanalAgora(String canalAgora);

	// Historial de llamadas de una conversación
	List<Llamada> findByConversacion_Id(Long conversacionId);

	// Llamadas activas o iniciadas de un usuario (para saber si ya tiene una en curso)
	List<Llamada> findByUsuarioLlamante_IdAndEstadoLlamadaIn(
			Long usuarioId, List<EstadoLlamada> estados);

	List<Llamada> findByUsuarioReceptor_IdAndEstadoLlamadaIn(
			Long usuarioId, List<EstadoLlamada> estados);

	// Historial completo de un usuario (como llamante o receptor)
	List<Llamada> findByUsuarioLlamante_Id(Long usuarioId);

	List<Llamada> findByUsuarioReceptor_Id(Long usuarioId);

}
