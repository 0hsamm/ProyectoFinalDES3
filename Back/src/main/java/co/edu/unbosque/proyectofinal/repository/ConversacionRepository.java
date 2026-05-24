package co.edu.unbosque.proyectofinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.proyectofinal.entity.Conversacion;

public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {

    List<Conversacion> findByParticipante_Usuario_Id(Long usuarioId);

    @Query("""
            select c
            from Conversacion c
            join c.participante p
            where p.usuario.id = :usuarioId
            and p.oculta = false
            order by c.fechaUltimoMensaje desc
            """)
    List<Conversacion> findVisiblesByUsuarioIdOrderByActividadDesc(
            @Param("usuarioId") Long usuarioId);
}
