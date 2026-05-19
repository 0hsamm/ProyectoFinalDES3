package co.edu.unbosque.proyectofinal.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.ParticipanteConversacion;
import co.edu.unbosque.proyectofinal.enums.RolParticipante;

public interface ParticipanteConversacionRepository extends JpaRepository<ParticipanteConversacion, Long> {

    boolean existsByConversacion_IdAndUsuario_Id(Long conversacionId, Long usuarioId);
    
    boolean existsByConversacion_IdAndUsuario_UsuarioAndRol(
            Long conversacionId,
            String usuario,
            RolParticipante rol);
    
    boolean existsByConversacion_IdAndUsuario_Usuario(Long conversacionId, String usuario);
}


