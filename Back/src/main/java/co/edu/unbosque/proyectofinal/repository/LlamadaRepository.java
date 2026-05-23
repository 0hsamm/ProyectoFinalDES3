package co.edu.unbosque.proyectofinal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.proyectofinal.entity.Llamada;
import co.edu.unbosque.proyectofinal.enums.EstadoLlamada;

@Repository
public interface LlamadaRepository extends JpaRepository<Llamada, Long> {

	Optional<Llamada> findByCanalAgora(String canalAgora);

	List<Llamada> findByConversacion_Id(Long conversacionId);

	List<Llamada> findByUsuarioLlamante_IdAndEstadoLlamadaIn(
			Long usuarioId, List<EstadoLlamada> estados);

	List<Llamada> findByUsuarioReceptor_IdAndEstadoLlamadaIn(
			Long usuarioId, List<EstadoLlamada> estados);

	List<Llamada> findByUsuarioLlamante_Id(Long usuarioId);

	List<Llamada> findByUsuarioReceptor_Id(Long usuarioId);

}
