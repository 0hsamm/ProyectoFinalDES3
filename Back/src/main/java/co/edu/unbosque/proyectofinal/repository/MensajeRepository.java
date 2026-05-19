package co.edu.unbosque.proyectofinal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.Mensaje;
import co.edu.unbosque.proyectofinal.entity.Usuario;

public interface MensajeRepository extends JpaRepository<Mensaje, Long>{

    List<Mensaje> findByConversacion_Id(Long conversacionId);

    List<Mensaje> findTop20ByConversacion_IdOrderByHoraEnvioDesc(Long conversacionId);

    Page<Mensaje> findByConversacion_Id(Long conversacionId, Pageable pageable);
    
    List<Mensaje> findByRemitente(Usuario remitente);

    Optional<Mensaje> findTop1ByConversacion_IdOrderByHoraEnvioDesc(Long conversacionId);
}