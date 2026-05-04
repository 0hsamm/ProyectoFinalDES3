package co.edu.unbosque.proyectofinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.ParticipanteConversacion;

public interface ParticipanteConversacionRepository extends JpaRepository<ParticipanteConversacion, Long> {

    List<ParticipanteConversacion> findByUsuarioId(Long usuarioId);

    List<ParticipanteConversacion> findByConversacionId(Long conversacionId);
}