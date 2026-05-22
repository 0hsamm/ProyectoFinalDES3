package co.edu.unbosque.proyectofinal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.SolicitudAmistad;
import co.edu.unbosque.proyectofinal.enums.EstadoSolicitudAmistad;

public interface SolicitudAmistadRepository
        extends JpaRepository<SolicitudAmistad, Long> {

    List<SolicitudAmistad> findByReceptor_IdAndEstado(
            Long receptorId,
            EstadoSolicitudAmistad estado);

    List<SolicitudAmistad> findBySolicitante_IdAndEstado(
            Long solicitanteId,
            EstadoSolicitudAmistad estado);

    Optional<SolicitudAmistad> findBySolicitante_IdAndReceptor_Id(
            Long solicitanteId,
            Long receptorId);
}
