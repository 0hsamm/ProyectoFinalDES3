package co.edu.unbosque.proyectofinal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.proyectofinal.entity.Estado;
import co.edu.unbosque.proyectofinal.entity.EstadoLike;
import co.edu.unbosque.proyectofinal.entity.Usuario;

public interface EstadoLikeRepository
        extends JpaRepository<EstadoLike, Long> {

    boolean existsByEstadoAndUsuario(
            Estado estado,
            Usuario usuario);

    Optional<EstadoLike> findByEstadoAndUsuario(
            Estado estado,
            Usuario usuario);

    List<EstadoLike> findByEstadoOrderByFechaLikeDesc(
            Estado estado);

    int countByEstado(
            Estado estado);

    void deleteByEstado(
            Estado estado);
}
